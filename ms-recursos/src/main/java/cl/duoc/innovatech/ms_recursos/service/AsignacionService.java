package cl.duoc.innovatech.ms_recursos.service;

import cl.duoc.innovatech.ms_recursos.dto.AsignacionDTO;
import cl.duoc.innovatech.ms_recursos.dto.DisponibilidadDTO;
import cl.duoc.innovatech.ms_recursos.dto.ResumenRecursosDTO;

import java.util.List;

public interface AsignacionService {
    AsignacionDTO crear(AsignacionDTO dto);
    AsignacionDTO obtenerPorId(Long id);
    List<AsignacionDTO> obtenerTodas();
    List<AsignacionDTO> obtenerPorEmpleado(Long empleadoId);
    List<AsignacionDTO> obtenerPorProyecto(Long proyectoId);
    AsignacionDTO actualizar(Long id, AsignacionDTO dto);
    void desactivar(Long id);
    List<DisponibilidadDTO> obtenerDisponibilidadEquipo();
    ResumenRecursosDTO obtenerResumen();
}
