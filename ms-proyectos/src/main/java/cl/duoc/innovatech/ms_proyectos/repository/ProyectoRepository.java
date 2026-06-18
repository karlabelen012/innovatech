package cl.duoc.innovatech.ms_proyectos.repository;

import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByEstado(String estado);
}
