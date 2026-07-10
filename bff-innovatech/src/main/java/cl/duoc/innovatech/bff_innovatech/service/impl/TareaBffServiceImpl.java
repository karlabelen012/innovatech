package cl.duoc.innovatech.bff_innovatech.service.impl;

import cl.duoc.innovatech.bff_innovatech.client.MsProyectosClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.TareaRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.TareaResponse;
import cl.duoc.innovatech.bff_innovatech.service.TareaBffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio BFF para tareas.
 * Delega al cliente HTTP de ms-proyectos (mismo microservicio que gestiona proyectos).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TareaBffServiceImpl implements TareaBffService {

    private final MsProyectosClient msProyectosClient;

    @Override
    public List<TareaResponse> listarPorProyecto(Long proyectoId) {
        log.info("BFF → ms-proyectos: listar tareas del proyecto id={}", proyectoId);
        return msProyectosClient.listarTareasPorProyecto(proyectoId);
    }

    @Override
    public TareaResponse obtenerPorId(Long id) {
        log.info("BFF → ms-proyectos: obtener tarea id={}", id);
        return msProyectosClient.obtenerTareaPorId(id);
    }

    @Override
    public TareaResponse crear(TareaRequest request) {
        log.info("BFF → ms-proyectos: crear tarea '{}'", request.getTitulo());
        return msProyectosClient.crearTarea(request);
    }

    @Override
    public TareaResponse actualizar(Long id, TareaRequest request) {
        log.info("BFF → ms-proyectos: actualizar tarea id={}", id);
        return msProyectosClient.actualizarTarea(id, request);
    }

    @Override
    public void eliminar(Long id) {
        log.info("BFF → ms-proyectos: eliminar tarea id={}", id);
        msProyectosClient.eliminarTarea(id);
    }
}
