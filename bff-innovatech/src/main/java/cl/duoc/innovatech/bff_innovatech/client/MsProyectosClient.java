package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.TareaRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.TareaResponse;
import cl.duoc.innovatech.bff_innovatech.exception.MicroservicioNoDisponibleException;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente HTTP del BFF hacia ms-proyectos (puerto 8081).
 * Cada método HTTP está protegido por un Circuit Breaker (Resilience4j):
 * si ms-proyectos cae, el circuito se abre y las llamadas fallan rápido
 * con MicroservicioNoDisponibleException, sin afectar a los demás servicios.
 *
 * Resilience4j despacha el fallback según el tipo de excepción más específico:
 * un RecursoNoEncontradoException (404 de negocio) se relanza tal cual mediante
 * el fallback específico, sin contar como fallo del microservicio ni afectar al circuito.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MsProyectosClient {

    private final RestTemplate restTemplate;

    @Value("${ms.proyectos.url}")
    private String baseUrl;

    private static final String BASE_PATH = "/api/v1/proyectos";
    private static final String TAREAS_PATH = "/api/v1/tareas";

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "listarProyectosFallback")
    public List<ProyectoResponse> listarProyectos() {
        log.debug("GET {}{}", baseUrl, BASE_PATH);
        ResponseEntity<List<ProyectoResponse>> resp = restTemplate.exchange(
                baseUrl + BASE_PATH,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<ProyectoResponse> listarProyectosFallback(Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "obtenerProyectoPorIdFallback")
    public ProyectoResponse obtenerProyectoPorId(Long id) {
        try {
            return restTemplate.getForObject(baseUrl + BASE_PATH + "/" + id, ProyectoResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado: " + id);
        }
    }

    private ProyectoResponse obtenerProyectoPorIdFallback(Long id, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private ProyectoResponse obtenerProyectoPorIdFallback(Long id, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "crearProyectoFallback")
    public ProyectoResponse crearProyecto(ProyectoRequest request) {
        HttpEntity<ProyectoRequest> entity = new HttpEntity<>(request, jsonHeaders());
        ResponseEntity<ProyectoResponse> resp = restTemplate.postForEntity(
                baseUrl + BASE_PATH, entity, ProyectoResponse.class);
        return resp.getBody();
    }

    private ProyectoResponse crearProyectoFallback(ProyectoRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "actualizarProyectoFallback")
    public ProyectoResponse actualizarProyecto(Long id, ProyectoRequest request) {
        try {
            HttpEntity<ProyectoRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<ProyectoResponse> resp = restTemplate.exchange(
                    baseUrl + BASE_PATH + "/" + id,
                    HttpMethod.PUT, entity, ProyectoResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado: " + id);
        }
    }

    private ProyectoResponse actualizarProyectoFallback(Long id, ProyectoRequest request, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private ProyectoResponse actualizarProyectoFallback(Long id, ProyectoRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "eliminarProyectoFallback")
    public void eliminarProyecto(Long id) {
        try {
            restTemplate.delete(baseUrl + BASE_PATH + "/" + id);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado: " + id);
        }
    }

    private void eliminarProyectoFallback(Long id, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private void eliminarProyectoFallback(Long id, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "listarPorEstadoFallback")
    public List<ProyectoResponse> listarPorEstado(String estado) {
        ResponseEntity<List<ProyectoResponse>> resp = restTemplate.exchange(
                baseUrl + BASE_PATH + "?estado=" + estado,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<ProyectoResponse> listarPorEstadoFallback(String estado, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    // ── TAREAS ──────────────────────────────────────────────────────────────

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "listarTareasPorProyectoFallback")
    public List<TareaResponse> listarTareasPorProyecto(Long proyectoId) {
        ResponseEntity<List<TareaResponse>> resp = restTemplate.exchange(
                baseUrl + TAREAS_PATH + "/proyecto/" + proyectoId,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<TareaResponse> listarTareasPorProyectoFallback(Long proyectoId, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "obtenerTareaPorIdFallback")
    public TareaResponse obtenerTareaPorId(Long id) {
        try {
            return restTemplate.getForObject(baseUrl + TAREAS_PATH + "/" + id, TareaResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Tarea no encontrada: " + id);
        }
    }

    private TareaResponse obtenerTareaPorIdFallback(Long id, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private TareaResponse obtenerTareaPorIdFallback(Long id, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "crearTareaFallback")
    public TareaResponse crearTarea(TareaRequest request) {
        HttpEntity<TareaRequest> entity = new HttpEntity<>(request, jsonHeaders());
        ResponseEntity<TareaResponse> resp = restTemplate.postForEntity(
                baseUrl + TAREAS_PATH, entity, TareaResponse.class);
        return resp.getBody();
    }

    private TareaResponse crearTareaFallback(TareaRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "actualizarTareaFallback")
    public TareaResponse actualizarTarea(Long id, TareaRequest request) {
        try {
            HttpEntity<TareaRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<TareaResponse> resp = restTemplate.exchange(
                    baseUrl + TAREAS_PATH + "/" + id,
                    HttpMethod.PUT, entity, TareaResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Tarea no encontrada: " + id);
        }
    }

    private TareaResponse actualizarTareaFallback(Long id, TareaRequest request, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private TareaResponse actualizarTareaFallback(Long id, TareaRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    @CircuitBreaker(name = "msProyectos", fallbackMethod = "eliminarTareaFallback")
    public void eliminarTarea(Long id) {
        try {
            restTemplate.delete(baseUrl + TAREAS_PATH + "/" + id);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Tarea no encontrada: " + id);
        }
    }

    private void eliminarTareaFallback(Long id, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private void eliminarTareaFallback(Long id, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
