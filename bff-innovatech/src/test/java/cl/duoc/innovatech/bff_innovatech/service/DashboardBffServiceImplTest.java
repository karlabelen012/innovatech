package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.client.MsAnaliticaClient;
import cl.duoc.innovatech.bff_innovatech.client.MsRecursosClient;
import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.exception.MicroservicioNoDisponibleException;
import cl.duoc.innovatech.bff_innovatech.service.impl.DashboardBffServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardBffServiceImpl - Pruebas Unitarias")
class DashboardBffServiceImplTest {

    @Mock
    private MsAnaliticaClient msAnaliticaClient;

    @Mock
    private MsRecursosClient msRecursosClient;

    @InjectMocks
    private DashboardBffServiceImpl service;

    private DashboardResponse dashboardAnalitica;
    private ResumenRecursosResponse resumenRR;

    @BeforeEach
    void setUp() {
        dashboardAnalitica = DashboardResponse.builder()
                .totalProyectos(15L)
                .proyectosActivos(8L)
                .proyectosFinalizados(5L)
                .proyectosPendientes(2L)
                .promedioAvance(62.5)
                .proyectosPorEstado(Map.of("EN_PROGRESO", 8L, "FINALIZADO", 5L))
                .build();

        resumenRR = ResumenRecursosResponse.builder()
                .totalEmpleados(12L)
                .empleadosDisponibles(4L)
                .empleadosOcupados(8L)
                .asignacionesActivas(15L)
                .build();
    }

    // ── DASHBOARD CONSOLIDADO ─────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerDashboardConsolidado() - combina datos de analítica y recursos")
    void dashboard_debeCombinarAnaliticaYRecursos() {
        when(msAnaliticaClient.obtenerDashboard()).thenReturn(dashboardAnalitica);
        when(msRecursosClient.obtenerResumen()).thenReturn(resumenRR);

        DashboardResponse resultado = service.obtenerDashboardConsolidado();

        // Datos de analítica
        assertThat(resultado.getTotalProyectos()).isEqualTo(15L);
        assertThat(resultado.getProyectosActivos()).isEqualTo(8L);
        assertThat(resultado.getPromedioAvance()).isEqualTo(62.5);

        // Datos enriquecidos de recursos
        assertThat(resultado.getTotalEmpleados()).isEqualTo(12L);
        assertThat(resultado.getEmpleadosDisponibles()).isEqualTo(4L);
        assertThat(resultado.getAsignacionesActivas()).isEqualTo(15L);

        verify(msAnaliticaClient).obtenerDashboard();
        verify(msRecursosClient).obtenerResumen();
    }

    @Test
    @DisplayName("obtenerDashboardConsolidado() - funciona parcialmente si ms-recursos no responde")
    void dashboard_debeRetornarParcialSiMsRecursosNoDisponible() {
        when(msAnaliticaClient.obtenerDashboard()).thenReturn(dashboardAnalitica);
        when(msRecursosClient.obtenerResumen())
                .thenThrow(new MicroservicioNoDisponibleException("ms-recursos", new RuntimeException("timeout")));

        // No debe lanzar excepción — debe devolver dashboard parcial
        DashboardResponse resultado = service.obtenerDashboardConsolidado();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalProyectos()).isEqualTo(15L);
        // Campos de recursos quedan nulos (sin datos)
        assertThat(resultado.getTotalEmpleados()).isNull();
    }

    @Test
    @DisplayName("obtenerDashboardConsolidado() - retorna objeto vacío si ms-analitica falla")
    void dashboard_retornaObjetoVacioSiAnaliticaRetornaNull() {
        when(msAnaliticaClient.obtenerDashboard()).thenReturn(null);
        when(msRecursosClient.obtenerResumen()).thenReturn(resumenRR);

        DashboardResponse resultado = service.obtenerDashboardConsolidado();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalEmpleados()).isEqualTo(12L);
    }

    // ── KPIs ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarKpis() - devuelve lista de KPIs")
    void listarKpis_debeRetornarLista() {
        KpiMetricaResponse kpi = KpiMetricaResponse.builder()
                .id(1L).nombreKpi("Proyectos Activos")
                .categoria("PROYECTOS").valor(8.0).unidad("proyectos")
                .build();
        when(msAnaliticaClient.listarKpis()).thenReturn(List.of(kpi));

        List<KpiMetricaResponse> resultado = service.listarKpis();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreKpi()).isEqualTo("Proyectos Activos");
        verify(msAnaliticaClient).listarKpis();
    }

    @Test
    @DisplayName("listarKpisPorCategoria() - filtra por categoría")
    void listarKpisPorCategoria_debeRetornarFiltrados() {
        KpiMetricaResponse kpi = KpiMetricaResponse.builder()
                .id(2L).nombreKpi("Empleados Disponibles")
                .categoria("RECURSOS").valor(4.0)
                .build();
        when(msAnaliticaClient.listarKpisPorCategoria("RECURSOS")).thenReturn(List.of(kpi));

        List<KpiMetricaResponse> resultado = service.listarKpisPorCategoria("RECURSOS");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo("RECURSOS");
    }

    @Test
    @DisplayName("listarKpis() - retorna lista vacía cuando no hay KPIs")
    void listarKpis_listaVacia() {
        when(msAnaliticaClient.listarKpis()).thenReturn(List.of());

        List<KpiMetricaResponse> resultado = service.listarKpis();

        assertThat(resultado).isEmpty();
    }
}
