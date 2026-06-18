package cl.duoc.innovatech.ms_recursos.controller;

import cl.duoc.innovatech.ms_recursos.dto.EmpleadoDTO;
import cl.duoc.innovatech.ms_recursos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import cl.duoc.innovatech.ms_recursos.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(EmpleadoController.class)
@DisplayName("Tests unitarios - EmpleadoController")
class EmpleadoControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpleadoService empleadoService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmpleadoDTO empleadoDTO;

    @BeforeEach
    void setUp() {
        empleadoDTO = EmpleadoDTO.builder()
                .id(1L).nombre("Bryan").apellido("Muñoz")
                .email("bryan@innovatech.cl").telefono("+56912345678")
                .rol(RolEmpleado.DESARROLLADOR)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(40).activo(true).build();
    }

    @Test
    @DisplayName("POST /api/v1/empleados - crear empleado retorna 201")
    void crearEmpleado_retorna201() throws Exception {
        when(empleadoService.crear(any())).thenReturn(empleadoDTO);

        mockMvc.perform(post("/api/v1/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Bryan"))
                .andExpect(jsonPath("$.email").value("bryan@innovatech.cl"))
                .andExpect(jsonPath("$.rol").value("DESARROLLADOR"));
    }

    @Test
    @DisplayName("GET /api/v1/empleados - listar todos retorna 200")
    void listarTodos_retorna200() throws Exception {
        when(empleadoService.obtenerTodos()).thenReturn(List.of(empleadoDTO));

        mockMvc.perform(get("/api/v1/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Bryan"));
    }

    @Test
    @DisplayName("GET /api/v1/empleados?activo=true - listar solo activos")
    void listarActivos_retorna200() throws Exception {
        when(empleadoService.obtenerActivos()).thenReturn(List.of(empleadoDTO));

        mockMvc.perform(get("/api/v1/empleados").param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/empleados?disponibilidad=DISPONIBLE - filtrar por disponibilidad")
    void listarPorDisponibilidad_retorna200() throws Exception {
        when(empleadoService.obtenerPorDisponibilidad(DisponibilidadEstado.DISPONIBLE))
                .thenReturn(List.of(empleadoDTO));

        mockMvc.perform(get("/api/v1/empleados").param("disponibilidad", "DISPONIBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/empleados?rol=DESARROLLADOR - filtrar por rol")
    void listarPorRol_retorna200() throws Exception {
        when(empleadoService.obtenerPorRol(RolEmpleado.DESARROLLADOR))
                .thenReturn(List.of(empleadoDTO));

        mockMvc.perform(get("/api/v1/empleados").param("rol", "DESARROLLADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/empleados/{id} - obtener por ID retorna 200")
    void obtenerPorId_retorna200() throws Exception {
        when(empleadoService.obtenerPorId(1L)).thenReturn(empleadoDTO);

        mockMvc.perform(get("/api/v1/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.apellido").value("Muñoz"));
    }

    @Test
    @DisplayName("GET /api/v1/empleados/{id} - no encontrado retorna 404")
    void obtenerPorId_noExiste_retorna404() throws Exception {
        when(empleadoService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Empleado no encontrado con id: 99"));

        mockMvc.perform(get("/api/v1/empleados/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/empleados/{id} - actualizar retorna 200")
    void actualizar_retorna200() throws Exception {
        when(empleadoService.actualizar(eq(1L), any())).thenReturn(empleadoDTO);

        mockMvc.perform(put("/api/v1/empleados/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Bryan"));
    }

    @Test
    @DisplayName("PATCH /api/v1/empleados/{id}?disponibilidad=OCUPADO - actualizar disponibilidad")
    void actualizarDisponibilidad_retorna200() throws Exception {
        EmpleadoDTO ocupado = EmpleadoDTO.builder().id(1L).nombre("Bryan").apellido("Muñoz")
                .email("bryan@innovatech.cl").rol(RolEmpleado.DESARROLLADOR)
                .disponibilidad(DisponibilidadEstado.OCUPADO).activo(true).build();

        when(empleadoService.actualizarDisponibilidad(1L, DisponibilidadEstado.OCUPADO)).thenReturn(ocupado);

        mockMvc.perform(patch("/api/v1/empleados/1").param("disponibilidad", "OCUPADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponibilidad").value("OCUPADO"));
    }

    @Test
    @DisplayName("DELETE /api/v1/empleados/{id} - eliminar retorna 204")
    void eliminar_retorna204() throws Exception {
        doNothing().when(empleadoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/empleados/1"))
                .andExpect(status().isNoContent());
    }
}
