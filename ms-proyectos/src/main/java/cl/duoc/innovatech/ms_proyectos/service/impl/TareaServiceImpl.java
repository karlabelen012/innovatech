package cl.duoc.innovatech.ms_proyectos.service.impl;

import cl.duoc.innovatech.ms_proyectos.dto.TareaDTO;
import cl.duoc.innovatech.ms_proyectos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_proyectos.model.Tarea;
import cl.duoc.innovatech.ms_proyectos.repository.TareaRepository;
import cl.duoc.innovatech.ms_proyectos.service.TareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TareaServiceImpl implements TareaService {

    private final TareaRepository tareaRepository;

    @Override
    public List<TareaDTO> listar() {
        return tareaRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<TareaDTO> listarPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoId(proyectoId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public TareaDTO obtenerPorId(Long id) {
        return toDTO(tareaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con id: " + id)));
    }

    @Override
    public TareaDTO crear(TareaDTO dto) {
        return toDTO(tareaRepository.save(toEntity(dto)));
    }

    @Override
    public TareaDTO actualizar(Long id, TareaDTO dto) {
        Tarea existente = tareaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con id: " + id));
        existente.setTitulo(dto.getTitulo());
        existente.setDescripcion(dto.getDescripcion());
        existente.setEstado(dto.getEstado());
        existente.setResponsable(dto.getResponsable());
        return toDTO(tareaRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        if (!tareaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Tarea no encontrada con id: " + id);
        }
        tareaRepository.deleteById(id);
    }

    private TareaDTO toDTO(Tarea t) {
        return TareaDTO.builder()
                .id(t.getId())
                .titulo(t.getTitulo())
                .descripcion(t.getDescripcion())
                .estado(t.getEstado())
                .responsable(t.getResponsable())
                .proyectoId(t.getProyectoId())
                .build();
    }

    private Tarea toEntity(TareaDTO dto) {
        Tarea.TareaBuilder builder = Tarea.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .responsable(dto.getResponsable())
                .proyectoId(dto.getProyectoId());
        if (dto.getEstado() != null) {
            builder.estado(dto.getEstado());
        }
        return builder.build();
    }
}
