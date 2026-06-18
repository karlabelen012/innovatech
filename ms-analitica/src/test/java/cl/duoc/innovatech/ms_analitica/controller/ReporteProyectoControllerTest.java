package cl.duoc.innovatech.ms_analitica.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.innovatech.ms_analitica.dto.ReporteProyectoDTO;
import cl.duoc.innovatech.ms_analitica.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import cl.duoc.innovatech.ms_analitica.service.ReporteProyectoService;

@WebMvcTest(ReporteProyectoController.class)
class ReporteProyectoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReporteProyectoService reporteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReporteProyectoDTO dto;

    @BeforeEach
    void setUp() {
        dto = ReporteProyectoDTO.builder()
                .id(1L)
                .nombreProyecto("Proyecto Alpha")
                .proyectoIdExterno(10L)
                .estado(EstadoProyecto.EN_PROGRESO)
                .porcentajeAvance(70)
                .build();
    }

    @Test
    void listarTodos_debeRetornar200() throws Exception {
        when(reporteService.listarTodos()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProyecto").value("Proyecto Alpha"));
    }

    @Test
    void obtenerPorId_existente_debeRetornar200() throws Exception {
        when(reporteService.obtenerPorId(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/v1/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreProyecto").value("Proyecto Alpha"));
    }

    @Test
    void obtenerPorId_noExistente_debeRetornar404() throws Exception {
        when(reporteService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("Reporte no encontrado"));
        mockMvc.perform(get("/api/v1/reportes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_valido_debeRetornar201() throws Exception {
        when(reporteService.crear(any(ReporteProyectoDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/v1/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreProyecto").value("Proyecto Alpha"));
    }

    @Test
    void actualizar_existente_debeRetornar200() throws Exception {
        when(reporteService.actualizar(eq(1L), any(ReporteProyectoDTO.class))).thenReturn(dto);
        mockMvc.perform(put("/api/v1/reportes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_existente_debeRetornar204() throws Exception {
        doNothing().when(reporteService).eliminar(1L);
        mockMvc.perform(delete("/api/v1/reportes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void calcularPromedioAvance_debeRetornar200() throws Exception {
        when(reporteService.calcularPromedioAvance()).thenReturn(72.5);
        mockMvc.perform(get("/api/v1/reportes/promedio-avance"))
                .andExpect(status().isOk())
                .andExpect(content().string("72.5"));
    }

    @Test
    void listarPorEstado_debeRetornar200() throws Exception {
        when(reporteService.listarPorEstado(EstadoProyecto.EN_PROGRESO)).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/reportes/estado/EN_PROGRESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("EN_PROGRESO"));
    }
}