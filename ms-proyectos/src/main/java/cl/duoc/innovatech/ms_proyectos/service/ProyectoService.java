package cl.duoc.innovatech.ms_proyectos.service;

import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import cl.duoc.innovatech.ms_proyectos.repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public List<Proyecto> listar() {
        return proyectoRepository.findAll();
    }

    public Optional<Proyecto> buscarPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public Optional<Proyecto> actualizar(Long id, Proyecto datos) {
        return proyectoRepository.findById(id).map(p -> {
            p.setNombre(datos.getNombre());
            p.setDescripcion(datos.getDescripcion());
            p.setEstado(datos.getEstado());
            p.setAvance(datos.getAvance());
            p.setResponsable(datos.getResponsable());
            return proyectoRepository.save(p);
        });
    }

    public boolean eliminar(Long id) {
        if (proyectoRepository.existsById(id)) {
            proyectoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Proyecto> listarPorEstado(String estado) {
        return proyectoRepository.findByEstado(estado);
    }
}
