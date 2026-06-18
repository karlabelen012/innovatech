package cl.duoc.innovatech.ms_recursos.repository;

import cl.duoc.innovatech.ms_recursos.model.Empleado;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findByEmail(String email);

    List<Empleado> findByActivoTrue();

    List<Empleado> findByDisponibilidad(DisponibilidadEstado disponibilidad);

    List<Empleado> findByRol(RolEmpleado rol);

    List<Empleado> findByDisponibilidadAndActivoTrue(DisponibilidadEstado disponibilidad);

    List<Empleado> findByRolAndActivoTrue(RolEmpleado rol);

    boolean existsByEmail(String email);

    long countByDisponibilidad(DisponibilidadEstado disponibilidad);

    long countByActivoTrue();

    @Query("SELECT e FROM Empleado e WHERE e.activo = true AND e.habilidades LIKE %:habilidad%")
    List<Empleado> findByHabilidad(@Param("habilidad") String habilidad);
}
