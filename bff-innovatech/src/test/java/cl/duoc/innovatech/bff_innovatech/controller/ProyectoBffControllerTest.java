package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.bff_innovatech.service.ProyectoBffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProyectoBffController.class)
@DisplayName("ProyectoBffController - Pruebas Unitarias (MockMvc)")
class ProyectoBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProyectoBffService proyectoBffService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProyectoResponse proyectoMock;

    @BeforeEach
    void setUp() {
        proyectoMock = ProyectoResponse.builder()
                .id(1L).nombre("Sistema CRM")
                .estado("EN_PROGRESO").avance(45)
                .responsable("Bryan Muñoz").build();
    }

    @Test
    @DisplayName("GET /api/bff/proyectos - retorna 200 con lista")
    void listar_retorna200() throws Exception {
        when(proyectoBffService.listar()).thenReturn(List.of(proyectoMock));

        mockMvc.perform(get("/api/bff/proyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Sistema CRM"));
    }

    @Test
    @DisplayName("GET /api/bff/proyectos?estado=EN_PROGRESO - filtra por estado")
    void listarPorEstado_retorna200() throws Exception {
        when(proyectoBffService.listarPorEstado("EN_PROGRESO")).thenReturn(List.of(proyectoMock));

        mockMvc.perform(get("/api/bff/proyectos").param("estado", "EN_PROGRESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("EN_PROGRESO"));
    }

    @Test
    @DisplayName("GET /api/bff/proyectos/{id} - retorna 200 con proyecto")
    void obtenerPorId_retorna200() throws Exception {
        when(proyectoBffService.obtenerPorId(1L)).thenReturn(proyectoMock);

        mockMvc.perform(get("/api/bff/proyectos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Sistema CRM"));
    }

    @Test
    @DisplayName("GET /api/bff/proyectos/{id} - retorna 404 si no existe")
    void obtenerPorId_retorna404() throws Exception {
        when(proyectoBffService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Proyecto no encontrado: 99"));

        mockMvc.perform(get("/api/bff/proyectos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/bff/proyectos - retorna 201 al crear")
    void crear_retorna201() throws Exception {
        ProyectoRequest request = ProyectoRequest.builder()
                .nombre("Sistema CRM").estado("PENDIENTE").avance(0).build();
        when(proyectoBffService.crear(any())).thenReturn(proyectoMock);

        mockMvc.perform(post("/api/bff/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Sistema CRM"));
    }

    @Test
    @DisplayName("POST /api/bff/proyectos - retorna 400 si nombre vacío")
    void crear_retorna400SiNombreVacio() throws Exception {
        ProyectoRequest request = ProyectoRequest.builder().nombre("").build();

        mockMvc.perform(post("/api/bff/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/bff/proyectos/{id} - retorna 200 al actualizar")
    void actualizar_retorna200() throws Exception {
        ProyectoRequest request = ProyectoRequest.builder().nombre("Sistema CRM v2").build();
        when(proyectoBffService.actualizar(eq(1L), any())).thenReturn(proyectoMock);

        mockMvc.perform(put("/api/bff/proyectos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/bff/proyectos/{id} - retorna 204")
    void eliminar_retorna204() throws Exception {
        doNothing().when(proyectoBffService).eliminar(1L);

        mockMvc.perform(delete("/api/bff/proyectos/1"))
                .andExpect(status().isNoContent());
    }
}
