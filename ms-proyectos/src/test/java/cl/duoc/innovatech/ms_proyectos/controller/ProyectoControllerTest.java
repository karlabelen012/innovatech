package cl.duoc.innovatech.ms_proyectos.controller;

import cl.duoc.innovatech.ms_proyectos.dto.ProyectoDTO;
import cl.duoc.innovatech.ms_proyectos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_proyectos.service.ProyectoService;
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

@WebMvcTest(ProyectoController.class)
class ProyectoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProyectoService proyectoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProyectoDTO dto;

    @BeforeEach
    void setUp() {
        dto = ProyectoDTO.builder()
                .id(1L)
                .nombre("Portal Clientes")
                .descripcion("Sistema de gestión de clientes")
                .estado("PENDIENTE")
                .avance(0)
                .responsable("Bryan Muñoz")
                .build();
    }

    @Test
    void listar_debeRetornar200() throws Exception {
        when(proyectoService.listar()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/proyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Portal Clientes"));
    }

    @Test
    void listar_conQueryEstado_filtraPorEstado() throws Exception {
        when(proyectoService.listarPorEstado("PENDIENTE")).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/proyectos").param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void obtenerPorId_existente_debeRetornar200() throws Exception {
        when(proyectoService.obtenerPorId(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/v1/proyectos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Portal Clientes"));
    }

    @Test
    void obtenerPorId_noExistente_debeRetornar404() throws Exception {
        when(proyectoService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("Proyecto no encontrado"));
        mockMvc.perform(get("/api/v1/proyectos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_valido_debeRetornar201() throws Exception {
        when(proyectoService.crear(any(ProyectoDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/v1/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Portal Clientes"));
    }

    @Test
    void crear_sinNombre_debeRetornar400() throws Exception {
        dto.setNombre(null);
        mockMvc.perform(post("/api/v1/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_existente_debeRetornar200() throws Exception {
        when(proyectoService.actualizar(eq(1L), any(ProyectoDTO.class))).thenReturn(dto);
        mockMvc.perform(put("/api/v1/proyectos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_noExistente_debeRetornar404() throws Exception {
        when(proyectoService.actualizar(eq(99L), any(ProyectoDTO.class)))
                .thenThrow(new RecursoNoEncontradoException("Proyecto no encontrado"));
        mockMvc.perform(put("/api/v1/proyectos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_existente_debeRetornar204() throws Exception {
        doNothing().when(proyectoService).eliminar(1L);
        mockMvc.perform(delete("/api/v1/proyectos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_noExistente_debeRetornar404() throws Exception {
        org.mockito.Mockito.doThrow(new RecursoNoEncontradoException("Proyecto no encontrado"))
                .when(proyectoService).eliminar(99L);
        mockMvc.perform(delete("/api/v1/proyectos/99"))
                .andExpect(status().isNotFound());
    }
}
