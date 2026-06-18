package cl.duoc.innovatech.ms_analitica.service;

import cl.duoc.innovatech.ms_analitica.dto.ReporteProyectoDTO;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;

import java.util.List;

public interface ReporteProyectoService {
    List<ReporteProyectoDTO> listarTodos();
    ReporteProyectoDTO obtenerPorId(Long id);
    ReporteProyectoDTO obtenerPorProyectoExterno(Long proyectoIdExterno);
    ReporteProyectoDTO crear(ReporteProyectoDTO dto);
    ReporteProyectoDTO actualizar(Long id, ReporteProyectoDTO dto);
    void eliminar(Long id);
    List<ReporteProyectoDTO> listarPorEstado(EstadoProyecto estado);
    Double calcularPromedioAvance();
}
