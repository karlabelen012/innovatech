package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;

import java.util.List;

public interface ProyectoBffService {
    List<ProyectoResponse> listar();
    List<ProyectoResponse> listarPorEstado(String estado);
    ProyectoResponse obtenerPorId(Long id);
    ProyectoResponse crear(ProyectoRequest request);
    ProyectoResponse actualizar(Long id, ProyectoRequest request);
    void eliminar(Long id);
}
