package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.TareaRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.TareaResponse;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.bff_innovatech.service.TareaBffService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TareaBffController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TareaBffController - Pruebas Unitarias (MockMvc)")
class TareaBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TareaBffService tareaBffService;

    @Autowired
    private ObjectMapper objectMapper;

    private TareaResponse tareaMock;

    @BeforeEach
    void setUp() {
        tareaMock = TareaResponse.builder()
                .id(1L).titulo("Diseñar base de datos").estado("PENDIENTE")
                .responsable("Bryan Muñoz").proyectoId(1L).build();
    }

    @Test
    @DisplayName("GET /api/bff/tareas/proyecto/{id} - retorna 200 con lista")
    void listarPorProyecto_retorna200() throws Exception {
        when(tareaBffService.listarPorProyecto(1L)).thenReturn(List.of(tareaMock));

        mockMvc.perform(get("/api/bff/tareas/proyecto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Diseñar base de datos"));
    }

    @Test
    @DisplayName("GET /api/bff/tareas/{id} - retorna 200 con tarea")
    void obtenerPorId_retorna200() throws Exception {
        when(tareaBffService.obtenerPorId(1L)).thenReturn(tareaMock);

        mockMvc.perform(get("/api/bff/tareas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/bff/tareas/{id} - retorna 404 si no existe")
    void obtenerPorId_retorna404() throws Exception {
        when(tareaBffService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Tarea no encontrada: 99"));

        mockMvc.perform(get("/api/bff/tareas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/bff/tareas - retorna 201 al crear")
    void crear_retorna201() throws Exception {
        TareaRequest request = TareaRequest.builder().titulo("Diseñar base de datos").proyectoId(1L).build();
        when(tareaBffService.crear(any())).thenReturn(tareaMock);

        mockMvc.perform(post("/api/bff/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Diseñar base de datos"));
    }

    @Test
    @DisplayName("POST /api/bff/tareas - retorna 400 si falta proyectoId")
    void crear_retorna400SiFaltaProyecto() throws Exception {
        TareaRequest request = TareaRequest.builder().titulo("Diseñar base de datos").build();

        mockMvc.perform(post("/api/bff/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/bff/tareas/{id} - retorna 200 al actualizar")
    void actualizar_retorna200() throws Exception {
        TareaRequest request = TareaRequest.builder().titulo("Implementar API").proyectoId(1L).build();
        when(tareaBffService.actualizar(eq(1L), any())).thenReturn(tareaMock);

        mockMvc.perform(put("/api/bff/tareas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/bff/tareas/{id} - retorna 204")
    void eliminar_retorna204() throws Exception {
        doNothing().when(tareaBffService).eliminar(1L);

        mockMvc.perform(delete("/api/bff/tareas/1"))
                .andExpect(status().isNoContent());
    }
}
