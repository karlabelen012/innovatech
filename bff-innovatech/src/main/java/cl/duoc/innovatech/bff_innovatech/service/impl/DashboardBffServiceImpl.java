package cl.duoc.innovatech.bff_innovatech.service.impl;

import cl.duoc.innovatech.bff_innovatech.client.MsAnaliticaClient;
import cl.duoc.innovatech.bff_innovatech.client.MsProyectosClient;
import cl.duoc.innovatech.bff_innovatech.client.MsRecursosClient;
import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.service.DashboardBffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio BFF del Dashboard consolidado.
 * Combina datos de ms-analitica, ms-proyectos y ms-recursos en un único DTO
 * para reducir el número de llamadas HTTP desde el frontend.
 * Esto es la esencia del patrón BFF: agregar datos de múltiples servicios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardBffServiceImpl implements DashboardBffService {

    private static final int MAX_PROYECTOS_RECIENTES = 5;

    private final MsAnaliticaClient msAnaliticaClient;
    private final MsProyectosClient msProyectosClient;
    private final MsRecursosClient  msRecursosClient;

    @Override
    public DashboardResponse obtenerDashboardConsolidado() {
        log.info("BFF: construyendo dashboard consolidado");

        // 1. Obtener datos base de analítica (KPIs por categoría y destacados)
        DashboardResponse dashboard = msAnaliticaClient.obtenerDashboard();
        if (dashboard == null) {
            dashboard = DashboardResponse.builder().build();
        }

        // 2. Sobrescribir las cifras de proyectos con datos en vivo de ms-proyectos,
        // para que reflejen inmediatamente cualquier alta/edición/baja de proyectos.
        try {
            aplicarEstadisticasDeProyectos(dashboard, msProyectosClient.listarProyectos());
        } catch (Exception ex) {
            log.warn("ms-proyectos no disponible, estadísticas de proyectos no actualizadas: {}", ex.getMessage());
        }

        // 3. Enriquecer con resumen de recursos humanos
        try {
            ResumenRecursosResponse resumenRR = msRecursosClient.obtenerResumen();
            dashboard.setTotalEmpleados(resumenRR.getTotalEmpleados());
            dashboard.setEmpleadosDisponibles(resumenRR.getEmpleadosDisponibles());
            dashboard.setEmpleadosOcupados(resumenRR.getEmpleadosOcupados());
            dashboard.setAsignacionesActivas(resumenRR.getAsignacionesActivas());
        } catch (Exception ex) {
            log.warn("ms-recursos no disponible, dashboard parcial: {}", ex.getMessage());
        }

        return dashboard;
    }

    private void aplicarEstadisticasDeProyectos(DashboardResponse dashboard, List<ProyectoResponse> proyectos) {
        if (proyectos == null) return;

        Map<String, Long> porEstado = proyectos.stream()
                .collect(Collectors.groupingBy(ProyectoResponse::getEstado, Collectors.counting()));

        double promedioAvance = proyectos.stream()
                .filter(p -> "EN_PROGRESO".equals(p.getEstado()) && p.getAvance() != null)
                .mapToInt(ProyectoResponse::getAvance)
                .average()
                .orElse(0.0);

        List<ProyectoResponse> recientes = proyectos.stream()
                .sorted(Comparator.comparing(ProyectoResponse::getId).reversed())
                .limit(MAX_PROYECTOS_RECIENTES)
                .collect(Collectors.toList());

        dashboard.setTotalProyectos((long) proyectos.size());
        dashboard.setProyectosActivos(porEstado.getOrDefault("EN_PROGRESO", 0L));
        dashboard.setProyectosFinalizados(porEstado.getOrDefault("FINALIZADO", 0L));
        dashboard.setProyectosPendientes(porEstado.getOrDefault("PENDIENTE", 0L));
        dashboard.setPromedioAvance(promedioAvance);
        dashboard.setProyectosPorEstado(porEstado);
        dashboard.setProyectosRecientes(recientes);
    }

    @Override
    public List<KpiMetricaResponse> listarKpis() {
        log.info("BFF → ms-analitica: listar KPIs");
        return msAnaliticaClient.listarKpis();
    }

    @Override
    public List<KpiMetricaResponse> listarKpisPorCategoria(String categoria) {
        log.info("BFF → ms-analitica: KPIs por categoría={}", categoria);
        return msAnaliticaClient.listarKpisPorCategoria(categoria);
    }
}
