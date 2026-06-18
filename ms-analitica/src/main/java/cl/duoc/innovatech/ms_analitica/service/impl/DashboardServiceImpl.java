package cl.duoc.innovatech.ms_analitica.service.impl;

import cl.duoc.innovatech.ms_analitica.dto.DashboardDTO;
import cl.duoc.innovatech.ms_analitica.dto.KpiMetricaDTO;
import cl.duoc.innovatech.ms_analitica.dto.ReporteProyectoDTO;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import cl.duoc.innovatech.ms_analitica.repository.KpiMetricaRepository;
import cl.duoc.innovatech.ms_analitica.repository.ReporteProyectoRepository;
import cl.duoc.innovatech.ms_analitica.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ReporteProyectoRepository reporteRepository;
    private final KpiMetricaRepository kpiRepository;

    @Override
    public DashboardDTO obtenerDashboard() {
        long total = reporteRepository.count();
        long activos = reporteRepository.countByEstado(EstadoProyecto.EN_PROGRESO);
        long finalizados = reporteRepository.countByEstado(EstadoProyecto.FINALIZADO);
        long pendientes = reporteRepository.countByEstado(EstadoProyecto.PENDIENTE);
        Double promedio = reporteRepository.calcularPromedioAvanceActivos();
        long totalKpis = kpiRepository.count();

        Map<String, Long> porEstado = new HashMap<>();
        for (EstadoProyecto estado : EstadoProyecto.values()) {
            porEstado.put(estado.name(), reporteRepository.countByEstado(estado));
        }

        List<Object[]> promediosRaw = kpiRepository.promedioValorPorCategoria();
        Map<String, Double> kpisPorCategoria = new HashMap<>();
        for (Object[] fila : promediosRaw) {
            kpisPorCategoria.put(fila[0].toString(), (Double) fila[1]);
        }

        List<ReporteProyectoDTO> recientes = reporteRepository.findTop5ByOrderByFechaRegistroDesc()
                .stream().map(r -> ReporteProyectoDTO.builder()
                        .id(r.getId())
                        .nombreProyecto(r.getNombreProyecto())
                        .estado(r.getEstado())
                        .porcentajeAvance(r.getPorcentajeAvance())
                        .proyectoIdExterno(r.getProyectoIdExterno())
                        .build()).collect(Collectors.toList());

        List<KpiMetricaDTO> kpisDestacados = kpiRepository.findTop5ByOrderByFechaCalculoDesc()
                .stream().map(k -> KpiMetricaDTO.builder()
                        .id(k.getId())
                        .nombreKpi(k.getNombreKpi())
                        .categoria(k.getCategoria())
                        .valor(k.getValor())
                        .unidad(k.getUnidad())
                        .build()).collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalProyectos(total)
                .proyectosActivos(activos)
                .proyectosFinalizados(finalizados)
                .proyectosPendientes(pendientes)
                .promedioAvance(promedio != null ? promedio : 0.0)
                .totalKpis(totalKpis)
                .proyectosPorEstado(porEstado)
                .kpisPorCategoria(kpisPorCategoria)
                .proyectosRecientes(recientes)
                .kpisDestacados(kpisDestacados)
                .build();
    }
}
