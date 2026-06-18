package cl.duoc.innovatech.ms_analitica.repository;

import cl.duoc.innovatech.ms_analitica.model.ReporteProyecto;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteProyectoRepository extends JpaRepository<ReporteProyecto, Long> {

    List<ReporteProyecto> findByEstado(EstadoProyecto estado);

    Optional<ReporteProyecto> findByProyectoIdExterno(Long proyectoIdExterno);

    Long countByEstado(EstadoProyecto estado);

    @Query("SELECT AVG(r.porcentajeAvance) FROM ReporteProyecto r WHERE r.estado = 'EN_PROGRESO'")
    Double calcularPromedioAvanceActivos();

    List<ReporteProyecto> findTop5ByOrderByFechaRegistroDesc();
}
