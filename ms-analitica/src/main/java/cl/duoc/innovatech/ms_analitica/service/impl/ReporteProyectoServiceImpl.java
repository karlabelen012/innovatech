package cl.duoc.innovatech.ms_analitica.service.impl;

import cl.duoc.innovatech.ms_analitica.dto.ReporteProyectoDTO;
import cl.duoc.innovatech.ms_analitica.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_analitica.model.ReporteProyecto;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import cl.duoc.innovatech.ms_analitica.repository.ReporteProyectoRepository;
import cl.duoc.innovatech.ms_analitica.service.ReporteProyectoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteProyectoServiceImpl implements ReporteProyectoService {

    private final ReporteProyectoRepository reporteRepository;

    @Override
    public List<ReporteProyectoDTO> listarTodos() {
        return reporteRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ReporteProyectoDTO obtenerPorId(Long id) {
        return toDTO(reporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado con id: " + id)));
    }

    @Override
    public ReporteProyectoDTO obtenerPorProyectoExterno(Long proyectoIdExterno) {
        return toDTO(reporteRepository.findByProyectoIdExterno(proyectoIdExterno)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado para proyecto externo: " + proyectoIdExterno)));
    }

    @Override
    public ReporteProyectoDTO crear(ReporteProyectoDTO dto) {
        ReporteProyecto entidad = toEntity(dto);
        return toDTO(reporteRepository.save(entidad));
    }

    @Override
    public ReporteProyectoDTO actualizar(Long id, ReporteProyectoDTO dto) {
        ReporteProyecto existente = reporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado con id: " + id));
        existente.setNombreProyecto(dto.getNombreProyecto());
        existente.setEstado(dto.getEstado());
        existente.setPorcentajeAvance(dto.getPorcentajeAvance());
        existente.setTotalTareas(dto.getTotalTareas());
        existente.setTareasCompletadas(dto.getTareasCompletadas());
        existente.setTareasPendientes(dto.getTareasPendientes());
        existente.setRecursosAsignados(dto.getRecursosAsignados());
        existente.setFechaInicio(dto.getFechaInicio());
        existente.setFechaFinEstimada(dto.getFechaFinEstimada());
        return toDTO(reporteRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Reporte no encontrado con id: " + id);
        }
        reporteRepository.deleteById(id);
    }

    @Override
    public List<ReporteProyectoDTO> listarPorEstado(EstadoProyecto estado) {
        return reporteRepository.findByEstado(estado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Double calcularPromedioAvance() {
        Double promedio = reporteRepository.calcularPromedioAvanceActivos();
        return promedio != null ? promedio : 0.0;
    }

    private ReporteProyectoDTO toDTO(ReporteProyecto e) {
        return ReporteProyectoDTO.builder()
                .id(e.getId())
                .nombreProyecto(e.getNombreProyecto())
                .proyectoIdExterno(e.getProyectoIdExterno())
                .estado(e.getEstado())
                .porcentajeAvance(e.getPorcentajeAvance())
                .totalTareas(e.getTotalTareas())
                .tareasCompletadas(e.getTareasCompletadas())
                .tareasPendientes(e.getTareasPendientes())
                .recursosAsignados(e.getRecursosAsignados())
                .fechaInicio(e.getFechaInicio())
                .fechaFinEstimada(e.getFechaFinEstimada())
                .build();
    }

    private ReporteProyecto toEntity(ReporteProyectoDTO dto) {
        return ReporteProyecto.builder()
                .nombreProyecto(dto.getNombreProyecto())
                .proyectoIdExterno(dto.getProyectoIdExterno())
                .estado(dto.getEstado())
                .porcentajeAvance(dto.getPorcentajeAvance())
                .totalTareas(dto.getTotalTareas())
                .tareasCompletadas(dto.getTareasCompletadas())
                .tareasPendientes(dto.getTareasPendientes())
                .recursosAsignados(dto.getRecursosAsignados())
                .fechaInicio(dto.getFechaInicio())
                .fechaFinEstimada(dto.getFechaFinEstimada())
                .build();
    }
}
