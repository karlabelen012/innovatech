package cl.duoc.innovatech.ms_analitica.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private Long totalProyectos;
    private Long proyectosActivos;
    private Long proyectosFinalizados;
    private Long proyectosPendientes;
    private Double promedioAvance;
    private Long totalKpis;
    private Map<String, Long> proyectosPorEstado;
    private Map<String, Double> kpisPorCategoria;
    private List<ReporteProyectoDTO> proyectosRecientes;
    private List<KpiMetricaDTO> kpisDestacados;
}
