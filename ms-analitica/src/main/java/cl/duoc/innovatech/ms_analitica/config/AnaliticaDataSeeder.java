package cl.duoc.innovatech.ms_analitica.config;

import cl.duoc.innovatech.ms_analitica.model.KpiMetrica;
import cl.duoc.innovatech.ms_analitica.model.ReporteProyecto;
import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import cl.duoc.innovatech.ms_analitica.repository.KpiMetricaRepository;
import cl.duoc.innovatech.ms_analitica.repository.ReporteProyectoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnaliticaDataSeeder implements CommandLineRunner {

    private final ReporteProyectoRepository reporteRepository;
    private final KpiMetricaRepository kpiRepository;

    @Override
    public void run(String... args) {
        if (reporteRepository.count() > 0 || kpiRepository.count() > 0) {
            log.info("La base de datos analítica ya contiene datos. Se omite el seed.");
            return;
        }

        List<ReporteProyecto> reportes = List.of(
                ReporteProyecto.builder()
                        .nombreProyecto("Portal Financiero")
                        .proyectoIdExterno(1001L)
                        .estado(EstadoProyecto.EN_PROGRESO)
                        .porcentajeAvance(78)
                        .totalTareas(18)
                        .tareasCompletadas(14)
                        .tareasPendientes(4)
                        .recursosAsignados(3)
                        .fechaInicio(LocalDate.now().minusDays(60))
                        .fechaFinEstimada(LocalDate.now().plusDays(30))
                        .fechaRegistro(LocalDateTime.now())
                        .build(),
                ReporteProyecto.builder()
                        .nombreProyecto("Plataforma de Operaciones")
                        .proyectoIdExterno(1002L)
                        .estado(EstadoProyecto.FINALIZADO)
                        .porcentajeAvance(100)
                        .totalTareas(12)
                        .tareasCompletadas(12)
                        .tareasPendientes(0)
                        .recursosAsignados(2)
                        .fechaInicio(LocalDate.now().minusDays(120))
                        .fechaFinEstimada(LocalDate.now().minusDays(10))
                        .fechaRegistro(LocalDateTime.now().minusDays(2))
                        .build(),
                ReporteProyecto.builder()
                        .nombreProyecto("Migración Cloud")
                        .proyectoIdExterno(1003L)
                        .estado(EstadoProyecto.PENDIENTE)
                        .porcentajeAvance(0)
                        .totalTareas(9)
                        .tareasCompletadas(0)
                        .tareasPendientes(9)
                        .recursosAsignados(1)
                        .fechaInicio(LocalDate.now().plusDays(5))
                        .fechaFinEstimada(LocalDate.now().plusDays(45))
                        .fechaRegistro(LocalDateTime.now().minusDays(1))
                        .build()
        );
        reporteRepository.saveAll(reportes);

        List<KpiMetrica> kpis = List.of(
                KpiMetrica.builder().nombreKpi("Eficiencia de entregas").categoria(CategoriaKpi.PROYECTOS).valor(82.0).unidad("%") .descripcion("Avance promedio de los proyectos").build(),
                KpiMetrica.builder().nombreKpi("Utilización de recursos").categoria(CategoriaKpi.RECURSOS).valor(78.0).unidad("%") .descripcion("Carga de recursos asignados").build(),
                KpiMetrica.builder().nombreKpi("Productividad del equipo").categoria(CategoriaKpi.PRODUCTIVIDAD).valor(91.0).unidad("%") .descripcion("Productividad promedio").build(),
                KpiMetrica.builder().nombreKpi("Margen operativo").categoria(CategoriaKpi.GENERAL).valor(68.0).unidad("%") .descripcion("Margen del negocio").build()
        );
        kpiRepository.saveAll(kpis);

        log.info("Seed analítico creado: {} reportes y {} KPIs.", reportes.size(), kpis.size());
    }
}
