package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.dto.request.TareaRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.TareaResponse;

import java.util.List;

public interface TareaBffService {
    List<TareaResponse> listarPorProyecto(Long proyectoId);
    TareaResponse obtenerPorId(Long id);
    TareaResponse crear(TareaRequest request);
    TareaResponse actualizar(Long id, TareaRequest request);
    void eliminar(Long id);
}
