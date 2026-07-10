package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.bff_innovatech.service.RecursosBffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecursosBffController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RecursosBffController - Pruebas Unitarias (MockMvc)")
class RecursosBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecursosBffService recursosBffService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmpleadoResponse empleadoMock;
    private AsignacionResponse asignacionMock;

    @BeforeEach
    void setUp() {
        empleadoMock = EmpleadoResponse.builder()
                .id(1L).nombre("Karla").apellido("Herrera").email("karla@innovatech.cl")
                .rol("DESARROLLADOR").disponibilidad("DISPONIBLE").activo(true).build();

        asignacionMock = AsignacionResponse.builder()
                .id(1L).empleadoId(1L).proyectoId(5L).nombreProyecto("Portal Fintech")
                .fechaInicio(LocalDate.now()).activo(true).build();
    }

    @Test
    @DisplayName("GET /api/bff/empleados - retorna 200 con lista")
    void listarEmpleados_retorna200() throws Exception {
        when(recursosBffService.listarEmpleados()).thenReturn(List.of(empleadoMock));

        mockMvc.perform(get("/api/bff/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Karla"));
    }

    @Test
    @DisplayName("GET /api/bff/empleados/{id} - retorna 200 con empleado")
    void obtenerEmpleado_retorna200() throws Exception {
        when(recursosBffService.obtenerEmpleadoPorId(1L)).thenReturn(empleadoMock);

        mockMvc.perform(get("/api/bff/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("karla@innovatech.cl"));
    }

    @Test
    @DisplayName("GET /api/bff/empleados/{id} - retorna 404 si no existe")
    void obtenerEmpleado_retorna404() throws Exception {
        when(recursosBffService.obtenerEmpleadoPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Empleado no encontrado: 99"));

        mockMvc.perform(get("/api/bff/empleados/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/bff/empleados - retorna 201 al crear")
    void crearEmpleado_retorna201() throws Exception {
        EmpleadoRequest request = EmpleadoRequest.builder()
                .nombre("Karla").apellido("Herrera").email("karla@innovatech.cl").telefono("+56911111111").rol("DESARROLLADOR").build();
        when(recursosBffService.crearEmpleado(any())).thenReturn(empleadoMock);

        mockMvc.perform(post("/api/bff/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Karla"));
    }

    @Test
    @DisplayName("POST /api/bff/empleados - retorna 400 si falta email")
    void crearEmpleado_retorna400() throws Exception {
        EmpleadoRequest request = EmpleadoRequest.builder().nombre("Karla").apellido("Herrera").rol("DESARROLLADOR").build();

        mockMvc.perform(post("/api/bff/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/bff/empleados/{id} - retorna 200 al actualizar")
    void actualizarEmpleado_retorna200() throws Exception {
        EmpleadoRequest request = EmpleadoRequest.builder()
                .nombre("Karla").apellido("Herrera").email("karla@innovatech.cl").telefono("+56911111111").rol("GESTOR").build();
        when(recursosBffService.actualizarEmpleado(eq(1L), any())).thenReturn(empleadoMock);

        mockMvc.perform(put("/api/bff/empleados/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/bff/empleados/{id}/disponibilidad - retorna 200")
    void actualizarDisponibilidad_retorna200() throws Exception {
        when(recursosBffService.actualizarDisponibilidad(1L, "OCUPADO")).thenReturn(empleadoMock);

        mockMvc.perform(patch("/api/bff/empleados/1/disponibilidad").param("disponibilidad", "OCUPADO"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/bff/empleados/{id} - retorna 204")
    void eliminarEmpleado_retorna204() throws Exception {
        doNothing().when(recursosBffService).eliminarEmpleado(1L);

        mockMvc.perform(delete("/api/bff/empleados/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/bff/asignaciones - retorna 200 con lista")
    void listarAsignaciones_retorna200() throws Exception {
        when(recursosBffService.listarAsignaciones()).thenReturn(List.of(asignacionMock));

        mockMvc.perform(get("/api/bff/asignaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProyecto").value("Portal Fintech"));
    }

    @Test
    @DisplayName("POST /api/bff/asignaciones - retorna 201 al crear")
    void crearAsignacion_retorna201() throws Exception {
        AsignacionRequest request = AsignacionRequest.builder()
                .empleadoId(1L).proyectoId(5L).nombreProyecto("Portal Fintech").fechaInicio(LocalDate.now()).build();
        when(recursosBffService.crearAsignacion(any())).thenReturn(asignacionMock);

        mockMvc.perform(post("/api/bff/asignaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreProyecto").value("Portal Fintech"));
    }

    @Test
    @DisplayName("DELETE /api/bff/asignaciones/{id} - retorna 204")
    void desactivarAsignacion_retorna204() throws Exception {
        doNothing().when(recursosBffService).desactivarAsignacion(1L);

        mockMvc.perform(delete("/api/bff/asignaciones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/bff/recursos/resumen - retorna 200")
    void resumen_retorna200() throws Exception {
        ResumenRecursosResponse resumen = ResumenRecursosResponse.builder()
                .totalEmpleados(10L).empleadosDisponibles(6L).empleadosOcupados(4L).asignacionesActivas(7L).build();
        when(recursosBffService.obtenerResumen()).thenReturn(resumen);

        mockMvc.perform(get("/api/bff/recursos/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmpleados").value(10));
    }
}
