package cl.duoc.innovatech.ms_proyectos.service;

import cl.duoc.innovatech.ms_proyectos.dto.TareaDTO;

import java.util.List;

public interface TareaService {
    List<TareaDTO> listar();
    List<TareaDTO> listarPorProyecto(Long proyectoId);
    TareaDTO obtenerPorId(Long id);
    TareaDTO crear(TareaDTO dto);
    TareaDTO actualizar(Long id, TareaDTO dto);
    void eliminar(Long id);
}
