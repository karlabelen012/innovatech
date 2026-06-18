package cl.duoc.innovatech.ms_proyectos.service;

import cl.duoc.innovatech.ms_proyectos.model.Tarea;
import cl.duoc.innovatech.ms_proyectos.repository.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;

    public List<Tarea> listar() {
        return tareaRepository.findAll();
    }

    public List<Tarea> listarPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoId(proyectoId);
    }

    public Optional<Tarea> buscarPorId(Long id) {
        return tareaRepository.findById(id);
    }

    public Tarea guardar(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    public Optional<Tarea> actualizar(Long id, Tarea datos) {
        return tareaRepository.findById(id).map(t -> {
            t.setTitulo(datos.getTitulo());
            t.setDescripcion(datos.getDescripcion());
            t.setEstado(datos.getEstado());
            t.setResponsable(datos.getResponsable());
            return tareaRepository.save(t);
        });
    }

    public boolean eliminar(Long id) {
        if (tareaRepository.existsById(id)) {
            tareaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
