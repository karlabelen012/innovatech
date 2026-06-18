package cl.duoc.innovatech.ms_recursos.repository;

import cl.duoc.innovatech.ms_recursos.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByEmpleadoId(Long empleadoId);

    List<Asignacion> findByProyectoId(Long proyectoId);

    List<Asignacion> findByActivoTrue();

    List<Asignacion> findByEmpleadoIdAndActivoTrue(Long empleadoId);

    boolean existsByEmpleadoIdAndProyectoIdAndActivoTrue(Long empleadoId, Long proyectoId);

    long countByProyectoIdAndActivoTrue(Long proyectoId);

    @Query("SELECT COALESCE(SUM(a.horasAsignadas), 0) FROM Asignacion a WHERE a.empleado.id = :empleadoId AND a.activo = true")
    Integer sumHorasAsignadasByEmpleadoId(@Param("empleadoId") Long empleadoId);

    long countByActivoTrue();
}
