package cl.duoc.innovatech.ms_proyectos.service.impl;

import cl.duoc.innovatech.ms_proyectos.dto.ProyectoDTO;
import cl.duoc.innovatech.ms_proyectos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import cl.duoc.innovatech.ms_proyectos.repository.ProyectoRepository;
import cl.duoc.innovatech.ms_proyectos.service.ProyectoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;

    @Override
    public List<ProyectoDTO> listar() {
        return proyectoRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProyectoDTO> listarPorEstado(String estado) {
        return proyectoRepository.findByEstado(estado).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProyectoDTO obtenerPorId(Long id) {
        return toDTO(proyectoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado con id: " + id)));
    }

    @Override
    public ProyectoDTO crear(ProyectoDTO dto) {
        return toDTO(proyectoRepository.save(toEntity(dto)));
    }

    @Override
    public ProyectoDTO actualizar(Long id, ProyectoDTO dto) {
        Proyecto existente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado con id: " + id));
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setEstado(dto.getEstado());
        existente.setAvance(dto.getAvance());
        existente.setResponsable(dto.getResponsable());
        return toDTO(proyectoRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        if (!proyectoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado con id: " + id);
        }
        proyectoRepository.deleteById(id);
    }

    private ProyectoDTO toDTO(Proyecto p) {
        return ProyectoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .estado(p.getEstado())
                .avance(p.getAvance())
                .responsable(p.getResponsable())
                .build();
    }

    private Proyecto toEntity(ProyectoDTO dto) {
        Proyecto.ProyectoBuilder builder = Proyecto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .responsable(dto.getResponsable());
        if (dto.getEstado() != null) {
            builder.estado(dto.getEstado());
        }
        if (dto.getAvance() != null) {
            builder.avance(dto.getAvance());
        }
        return builder.build();
    }
}
