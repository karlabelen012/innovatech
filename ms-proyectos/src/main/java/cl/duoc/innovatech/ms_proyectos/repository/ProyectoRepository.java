package cl.duoc.innovatech.ms_proyectos.repository;

import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByEstado(String estado);
}
