package cl.duoc.innovatech.ms_proyectos.controller;

import cl.duoc.innovatech.ms_proyectos.dto.TareaDTO;
import cl.duoc.innovatech.ms_proyectos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_proyectos.service.TareaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TareaController.class)
class TareaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TareaService tareaService;

    @Autowired
    private ObjectMapper objectMapper;

    private TareaDTO dto;

    @BeforeEach
    void setUp() {
        dto = TareaDTO.builder()
                .id(1L)
                .titulo("Diseñar base de datos")
                .descripcion("Crear el modelo ER")
                .estado("PENDIENTE")
                .responsable("Bryan Muñoz")
                .proyectoId(1L)
                .build();
    }

    @Test
    void listar_debeRetornar200() throws Exception {
        when(tareaService.listar()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Diseñar base de datos"));
    }

    @Test
    void listarPorProyecto_debeRetornar200() throws Exception {
        when(tareaService.listarPorProyecto(1L)).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/tareas/proyecto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].proyectoId").value(1));
    }

    @Test
    void obtenerPorId_existente_debeRetornar200() throws Exception {
        when(tareaService.obtenerPorId(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/v1/tareas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Diseñar base de datos"));
    }

    @Test
    void obtenerPorId_noExistente_debeRetornar404() throws Exception {
        when(tareaService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("Tarea no encontrada"));
        mockMvc.perform(get("/api/v1/tareas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_valido_debeRetornar201() throws Exception {
        when(tareaService.crear(any(TareaDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/v1/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Diseñar base de datos"));
    }

    @Test
    void crear_sinTitulo_debeRetornar400() throws Exception {
        dto.setTitulo(null);
        mockMvc.perform(post("/api/v1/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_existente_debeRetornar200() throws Exception {
        when(tareaService.actualizar(eq(1L), any(TareaDTO.class))).thenReturn(dto);
        mockMvc.perform(put("/api/v1/tareas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_existente_debeRetornar204() throws Exception {
        doNothing().when(tareaService).eliminar(1L);
        mockMvc.perform(delete("/api/v1/tareas/1"))
                .andExpect(status().isNoContent());
    }
}
