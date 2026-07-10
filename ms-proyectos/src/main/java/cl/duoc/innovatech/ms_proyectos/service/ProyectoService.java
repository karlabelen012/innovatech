package cl.duoc.innovatech.ms_proyectos.service;

import cl.duoc.innovatech.ms_proyectos.dto.ProyectoDTO;

import java.util.List;

public interface ProyectoService {
    List<ProyectoDTO> listar();
    List<ProyectoDTO> listarPorEstado(String estado);
    ProyectoDTO obtenerPorId(Long id);
    ProyectoDTO crear(ProyectoDTO dto);
    ProyectoDTO actualizar(Long id, ProyectoDTO dto);
    void eliminar(Long id);
}
