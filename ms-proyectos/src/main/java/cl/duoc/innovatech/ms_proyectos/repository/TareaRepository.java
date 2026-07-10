package cl.duoc.innovatech.ms_proyectos.repository;

import cl.duoc.innovatech.ms_proyectos.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByProyectoId(Long proyectoId);
}
