package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.client.MsAnaliticaClient;
import cl.duoc.innovatech.bff_innovatech.client.MsProyectosClient;
import cl.duoc.innovatech.bff_innovatech.client.MsRecursosClient;
import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardBffServiceImpl - Pruebas Unitarias")
class DashboardBffServiceImplTest {

    @Mock
    private MsAnaliticaClient msAnaliticaClient;

    @Mock
    private MsProyectosClient msProyectosClient;

    @Mock
    private MsRecursosClient msRecursosClient;

    @InjectMocks
    private DashboardBffServiceImpl service;

    private DashboardResponse dashboardAnalitica;
    private List<ProyectoResponse> proyectos;
    private ResumenRecursosResponse resumenRR;

    @BeforeEach
    void setUp() {
        // ms-analitica solo aporta los KPIs; las cifras de proyectos las
        // recalcula el BFF a partir de ms-proyectos (datos en vivo).
        dashboardAnalitica = DashboardResponse.builder().build();

        proyectos = List.of(
                ProyectoResponse.builder().id(1L).nombre("Portal Financiero").estado("EN_PROGRESO").avance(70).build(),
                ProyectoResponse.builder().id(2L).nombre("Plataforma de Operaciones").estado("FINALIZADO").avance(100).build(),
                ProyectoResponse.builder().id(3L).nombre("Migración Cloud").estado("PENDIENTE").avance(0).build()
        );

        resumenRR = ResumenRecursosResponse.builder()
                .totalEmpleados(12L)
                .empleadosDisponibles(4L)
                .empleadosOcupados(8L)
                .asignacionesActivas(15L)
                .build();
    }

    // ── DASHBOARD CONSOLIDADO ─────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerDashboardConsolidado() - combina datos de analítica, proyectos y recursos")
    void dashboard_debeCombinarAnaliticaYRecursos() {
        when(msAnaliticaClient.obtenerDashboard()).thenReturn(dashboardAnalitica);
        when(msProyectosClient.listarProyectos()).thenReturn(proyectos);
        when(msRecursosClient.obtenerResumen()).thenReturn(resumenRR);

        DashboardResponse resultado = service.obtenerDashboardConsolidado();

        // Cifras de proyectos calculadas en vivo desde ms-proyectos
        assertThat(resultado.getTotalProyectos()).isEqualTo(3L);
        assertThat(resultado.getProyectosActivos()).isEqualTo(1L);
        assertThat(resultado.getProyectosFinalizados()).isEqualTo(1L);
        assertThat(resultado.getProyectosPendientes()).isEqualTo(1L);
        assertThat(resultado.getPromedioAvance()).isEqualTo(70.0);
        assertThat(resultado.getProyectosRecientes()).extracting(ProyectoResponse::getNombre)
                .containsExactly("Migración Cloud", "Plataforma de Operaciones", "Portal Financiero");

        // Datos enriquecidos de recursos
        assertThat(resultado.getTotalEmpleados()).isEqualTo(12L);
        assertThat(resultado.getEmpleadosDisponibles()).isEqualTo(4L);
        assertThat(resultado.getAsignacionesActivas()).isEqualTo(15L);

        verify(msAnaliticaClient).obtenerDashboard();
        verify(msProyectosClient).listarProyectos();
        verify(msRecursosClient).obtenerResumen();
    }

    @Test
    @DisplayName("obtenerDashboardConsolidado() - funciona parcialmente si ms-recursos no responde")
    void dashboard_debeRetornarParcialSiMsRecursosNoDisponible() {
        when(msAnaliticaClient.obtenerDashboard()).thenReturn(dashboardAnalitica);
        when(msProyectosClient.listarProyectos()).thenReturn(proyectos);
        when(msRecursosClient.obtenerResumen())
                .thenThrow(new MicroservicioNoDisponibleException("ms-recursos", new RuntimeException("timeout")));

        // No debe lanzar excepción — debe devolver dashboard parcial
        DashboardResponse resultado = service.obtenerDashboardConsolidado();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalProyectos()).isEqualTo(3L);
        // Campos de recursos quedan nulos (sin datos)
        assertThat(resultado.getTotalEmpleados()).isNull();
    }

    @Test
    @DisplayName("obtenerDashboardConsolidado() - mantiene cifras en cero si ms-proyectos no responde")
    void dashboard_debeRetornarParcialSiMsProyectosNoDisponible() {
        when(msAnaliticaClient.obtenerDashboard()).thenReturn(dashboardAnalitica);
        when(msProyectosClient.listarProyectos())
                .thenThrow(new MicroservicioNoDisponibleException("ms-proyectos", new RuntimeException("timeout")));
        when(msRecursosClient.obtenerResumen()).thenReturn(resumenRR);

        DashboardResponse resultado = service.obtenerDashboardConsolidado();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalProyectos()).isNull();
        assertThat(resultado.getTotalEmpleados()).isEqualTo(12L);
    }

    @Test
    @DisplayName("obtenerDashboardConsolidado() - retorna objeto vacío si ms-analitica falla")
    void dashboard_retornaObjetoVacioSiAnaliticaRetornaNull() {
        when(msAnaliticaClient.obtenerDashboard()).thenReturn(null);
        when(msProyectosClient.listarProyectos()).thenReturn(proyectos);
        when(msRecursosClient.obtenerResumen()).thenReturn(resumenRR);

        DashboardResponse resultado = service.obtenerDashboardConsolidado();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalEmpleados()).isEqualTo(12L);
        assertThat(resultado.getTotalProyectos()).isEqualTo(3L);
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
