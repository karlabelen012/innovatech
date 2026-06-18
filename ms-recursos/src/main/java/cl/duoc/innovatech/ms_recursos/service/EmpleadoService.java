package cl.duoc.innovatech.ms_recursos.service;

import cl.duoc.innovatech.ms_recursos.dto.EmpleadoDTO;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;

import java.util.List;

public interface EmpleadoService {
    EmpleadoDTO crear(EmpleadoDTO dto);
    EmpleadoDTO obtenerPorId(Long id);
    List<EmpleadoDTO> obtenerTodos();
    List<EmpleadoDTO> obtenerActivos();
    EmpleadoDTO actualizar(Long id, EmpleadoDTO dto);
    void eliminar(Long id);
    List<EmpleadoDTO> obtenerPorDisponibilidad(DisponibilidadEstado estado);
    List<EmpleadoDTO> obtenerPorRol(RolEmpleado rol);
    EmpleadoDTO actualizarDisponibilidad(Long id, DisponibilidadEstado estado);
    List<EmpleadoDTO> buscarPorHabilidad(String habilidad);
}
