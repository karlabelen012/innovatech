package cl.duoc.innovatech.bff_innovatech.service.impl;

import cl.duoc.innovatech.bff_innovatech.client.MsProyectosClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.service.ProyectoBffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio BFF para proyectos.
 * Delega al cliente HTTP y aplica lógica de adaptación de respuestas
 * según las necesidades del frontend (BFF pattern: reduce over-fetching).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProyectoBffServiceImpl implements ProyectoBffService {

    private final MsProyectosClient msProyectosClient;

    @Override
    public List<ProyectoResponse> listar() {
        log.info("BFF → ms-proyectos: listar todos");
        return msProyectosClient.listarProyectos();
    }

    @Override
    public List<ProyectoResponse> listarPorEstado(String estado) {
        log.info("BFF → ms-proyectos: listar por estado={}", estado);
        return msProyectosClient.listarPorEstado(estado);
    }

    @Override
    public ProyectoResponse obtenerPorId(Long id) {
        log.info("BFF → ms-proyectos: obtener id={}", id);
        return msProyectosClient.obtenerProyectoPorId(id);
    }

    @Override
    public ProyectoResponse crear(ProyectoRequest request) {
        log.info("BFF → ms-proyectos: crear proyecto '{}'", request.getNombre());
        return msProyectosClient.crearProyecto(request);
    }

    @Override
    public ProyectoResponse actualizar(Long id, ProyectoRequest request) {
        log.info("BFF → ms-proyectos: actualizar id={}", id);
        return msProyectosClient.actualizarProyecto(id, request);
    }

    @Override
    public void eliminar(Long id) {
        log.info("BFF → ms-proyectos: eliminar id={}", id);
        msProyectosClient.eliminarProyecto(id);
    }
}
