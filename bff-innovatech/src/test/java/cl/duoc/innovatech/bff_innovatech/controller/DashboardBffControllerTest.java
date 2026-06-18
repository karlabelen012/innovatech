package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import cl.duoc.innovatech.bff_innovatech.service.DashboardBffService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardBffController.class)
@DisplayName("DashboardBffController - Pruebas Unitarias (MockMvc)")
class DashboardBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardBffService dashboardBffService;

    @Test
    @DisplayName("GET /api/bff/dashboard - retorna 200 con dashboard consolidado")
    void dashboard_retorna200() throws Exception {
        DashboardResponse response = DashboardResponse.builder()
                .totalProyectos(10L)
                .proyectosActivos(6L)
                .totalEmpleados(12L)
                .empleadosDisponibles(4L)
                .promedioAvance(55.0)
                .build();
        when(dashboardBffService.obtenerDashboardConsolidado()).thenReturn(response);

        mockMvc.perform(get("/api/bff/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProyectos").value(10))
                .andExpect(jsonPath("$.totalEmpleados").value(12))
                .andExpect(jsonPath("$.promedioAvance").value(55.0));
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/kpis - retorna lista de KPIs")
    void kpis_retorna200() throws Exception {
        KpiMetricaResponse kpi = KpiMetricaResponse.builder()
                .id(1L).nombreKpi("Proyectos Activos")
                .categoria("PROYECTOS").valor(6.0)
                .build();
        when(dashboardBffService.listarKpis()).thenReturn(List.of(kpi));

        mockMvc.perform(get("/api/bff/dashboard/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombreKpi").value("Proyectos Activos"));
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/kpis/categoria?categoria=RECURSOS - filtra KPIs")
    void kpisPorCategoria_retorna200() throws Exception {
        KpiMetricaResponse kpi = KpiMetricaResponse.builder()
                .id(2L).nombreKpi("Empleados Disponibles")
                .categoria("RECURSOS").valor(4.0)
                .build();
        when(dashboardBffService.listarKpisPorCategoria("RECURSOS")).thenReturn(List.of(kpi));

        mockMvc.perform(get("/api/bff/dashboard/kpis/categoria").param("categoria", "RECURSOS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("RECURSOS"));
    }
}
