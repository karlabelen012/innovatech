package cl.duoc.innovatech.ms_analitica.controller;

import java.util.List;
import java.util.Map;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.innovatech.ms_analitica.dto.KpiMetricaDTO;
import cl.duoc.innovatech.ms_analitica.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;
import cl.duoc.innovatech.ms_analitica.service.KpiMetricaService;

@WebMvcTest(KpiMetricaController.class)
class KpiMetricaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KpiMetricaService kpiService;

    @Autowired
    private ObjectMapper objectMapper;

    private KpiMetricaDTO dto;

    @BeforeEach
    void setUp() {
        dto = KpiMetricaDTO.builder()
                .id(1L)
                .nombreKpi("Proyectos Activos")
                .categoria(CategoriaKpi.PROYECTOS)
                .valor(5.0)
                .unidad("proyectos")
                .build();
    }

    @Test
    void listarTodos_debeRetornar200() throws Exception {
        when(kpiService.listarTodos()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreKpi").value("Proyectos Activos"));
    }

    @Test
    void obtenerPorId_existente_debeRetornar200() throws Exception {
        when(kpiService.obtenerPorId(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/v1/kpis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreKpi").value("Proyectos Activos"));
    }

    @Test
    void obtenerPorId_noExistente_debeRetornar404() throws Exception {
        when(kpiService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("KPI no encontrado con ID: 99"));
        mockMvc.perform(get("/api/v1/kpis/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_valido_debeRetornar201() throws Exception {
        when(kpiService.crear(any(KpiMetricaDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/v1/kpis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void actualizar_existente_debeRetornar200() throws Exception {
        when(kpiService.actualizar(eq(1L), any(KpiMetricaDTO.class))).thenReturn(dto);
        mockMvc.perform(put("/api/v1/kpis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_existente_debeRetornar204() throws Exception {
        doNothing().when(kpiService).eliminar(1L);
        mockMvc.perform(delete("/api/v1/kpis/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listarPorCategoria_debeRetornar200() throws Exception {
        when(kpiService.listarPorCategoria(CategoriaKpi.PROYECTOS)).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/kpis/categoria/PROYECTOS"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPromedios_debeRetornar200() throws Exception {
        when(kpiService.obtenerPromediosPorCategoria()).thenReturn(Map.of("PROYECTOS", 5.0));
        mockMvc.perform(get("/api/v1/kpis/promedios"))
                .andExpect(status().isOk());
    }
}