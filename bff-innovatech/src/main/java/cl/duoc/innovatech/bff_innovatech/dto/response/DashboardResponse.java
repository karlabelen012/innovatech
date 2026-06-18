package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta del dashboard consolidado que el BFF arma
 * combinando datos de ms-analitica, ms-proyectos y ms-recursos.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardResponse {

    // Datos de ms-analitica
    private Long totalProyectos;
    private Long proyectosActivos;
    private Long proyectosFinalizados;
    private Long proyectosPendientes;
    private Double promedioAvance;
    private Map<String, Long>   proyectosPorEstado;
    private Map<String, Double> kpisPorCategoria;

    // Resumen de recursos (ms-recursos)
    private Long totalEmpleados;
    private Long empleadosDisponibles;
    private Long empleadosOcupados;
    private Long asignacionesActivas;

    // Listas reducidas para el panel principal
    private List<ProyectoResponse>    proyectosRecientes;
    private List<KpiMetricaResponse>  kpisDestacados;
}
