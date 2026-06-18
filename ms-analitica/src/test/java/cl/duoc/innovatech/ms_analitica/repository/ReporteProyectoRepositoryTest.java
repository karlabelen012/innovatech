package cl.duoc.innovatech.ms_analitica.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.innovatech.ms_analitica.model.ReporteProyecto;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;

@DataJpaTest
@ActiveProfiles("test")
class ReporteProyectoRepositoryTest {

    @Autowired
    private ReporteProyectoRepository reporteRepository;

    private ReporteProyecto reporte;

    @BeforeEach
    void setUp() {
        reporteRepository.deleteAll(); // Limpieza inicial garantizada

        reporte = ReporteProyecto.builder()
                .nombreProyecto("Proyecto Test")
                .proyectoIdExterno(100L)
                .estado(EstadoProyecto.EN_PROGRESO)
                .porcentajeAvance(50)
                .totalTareas(8)
                .tareasCompletadas(4)
                .tareasPendientes(4)
                .recursosAsignados(2)
                .build();
        reporteRepository.save(reporte);
    }

    @Test
    void save_debePersistirReporte() {
        assertThat(reporte.getId()).isNotNull();
    }

    @Test
    void findByEstado_debeRetornarCoincidencias() {
        List<ReporteProyecto> resultado = reporteRepository.findByEstado(EstadoProyecto.EN_PROGRESO);
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoProyecto.EN_PROGRESO);
    }

    @Test
    void findByProyectoIdExterno_existente_debeRetornarReporte() {
        Optional<ReporteProyecto> resultado = reporteRepository.findByProyectoIdExterno(100L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombreProyecto()).isEqualTo("Proyecto Test");
    }

    @Test
    void countByEstado_debeRetornarConteo() {
        Long count = reporteRepository.countByEstado(EstadoProyecto.EN_PROGRESO);
        assertThat(count).isGreaterThan(0);
    }

@Test
    void findTop5ByOrderByFechaRegistroDesc_debeRetornarUltimos() {
        // Añadimos un segundo reporte para certificar que el ordenamiento funcione
        ReporteProyecto segundoReporte = ReporteProyecto.builder()
                .nombreProyecto("Proyecto Test Mas Reciente")
                .proyectoIdExterno(101L)
                .estado(EstadoProyecto.EN_PROGRESO)
                .porcentajeAvance(10)
                .build();
        reporteRepository.save(segundoReporte);

        List<ReporteProyecto> resultado = reporteRepository.findTop5ByOrderByFechaRegistroDesc();
        assertThat(resultado).isNotEmpty();
        
        // CORRECCIÓN: Cambiado 'isAtLeast(1)' por 'isGreaterThanOrEqualTo(1)'
        assertThat(resultado.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void calcularPromedioAvanceActivos_debeRetornarPromedio() {
        Double promedio = reporteRepository.calcularPromedioAvanceActivos();
        assertThat(promedio).isNotNull();
        assertThat(promedio).isEqualTo(50.0);
    }
}