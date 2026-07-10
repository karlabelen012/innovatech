package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
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
 * Cliente HTTP del BFF hacia ms-recursos (puerto 8084).
 * Protegido por Circuit Breaker (Resilience4j): si ms-recursos cae,
 * el circuito se abre y las llamadas fallan rápido sin afectar a los demás servicios.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MsRecursosClient {

    private final RestTemplate restTemplate;

    @Value("${ms.recursos.url}")
    private String baseUrl;

    private static final String EMP  = "/api/v1/empleados";
    private static final String ASIG = "/api/v1/asignaciones";

    // ── EMPLEADOS ──────────────────────────────────────────────────────────────

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "listarEmpleadosFallback")
    public List<EmpleadoResponse> listarEmpleados() {
        ResponseEntity<List<EmpleadoResponse>> resp = restTemplate.exchange(
                baseUrl + EMP, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<EmpleadoResponse> listarEmpleadosFallback(Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "obtenerEmpleadoPorIdFallback")
    public EmpleadoResponse obtenerEmpleadoPorId(Long id) {
        try {
            return restTemplate.getForObject(baseUrl + EMP + "/" + id, EmpleadoResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Empleado no encontrado: " + id);
        }
    }

    private EmpleadoResponse obtenerEmpleadoPorIdFallback(Long id, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private EmpleadoResponse obtenerEmpleadoPorIdFallback(Long id, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "crearEmpleadoFallback")
    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {
        HttpEntity<EmpleadoRequest> entity = new HttpEntity<>(request, jsonHeaders());
        ResponseEntity<EmpleadoResponse> resp = restTemplate.postForEntity(
                baseUrl + EMP, entity, EmpleadoResponse.class);
        return resp.getBody();
    }

    private EmpleadoResponse crearEmpleadoFallback(EmpleadoRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "actualizarEmpleadoFallback")
    public EmpleadoResponse actualizarEmpleado(Long id, EmpleadoRequest request) {
        try {
            HttpEntity<EmpleadoRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<EmpleadoResponse> resp = restTemplate.exchange(
                    baseUrl + EMP + "/" + id,
                    HttpMethod.PUT, entity, EmpleadoResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Empleado no encontrado: " + id);
        }
    }

    private EmpleadoResponse actualizarEmpleadoFallback(Long id, EmpleadoRequest request, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private EmpleadoResponse actualizarEmpleadoFallback(Long id, EmpleadoRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "actualizarDisponibilidadFallback")
    public EmpleadoResponse actualizarDisponibilidad(Long id, String disponibilidad) {
        ResponseEntity<EmpleadoResponse> resp = restTemplate.exchange(
                baseUrl + EMP + "/" + id + "?disponibilidad=" + disponibilidad,
                HttpMethod.PATCH, null, EmpleadoResponse.class);
        return resp.getBody();
    }

    private EmpleadoResponse actualizarDisponibilidadFallback(Long id, String disponibilidad, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "eliminarEmpleadoFallback")
    public void eliminarEmpleado(Long id) {
        try {
            restTemplate.delete(baseUrl + EMP + "/" + id);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Empleado no encontrado: " + id);
        }
    }

    private void eliminarEmpleadoFallback(Long id, RecursoNoEncontradoException ex) {
        throw ex;
    }

    private void eliminarEmpleadoFallback(Long id, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    // ── ASIGNACIONES ───────────────────────────────────────────────────────────

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "listarAsignacionesFallback")
    public List<AsignacionResponse> listarAsignaciones() {
        ResponseEntity<List<AsignacionResponse>> resp = restTemplate.exchange(
                baseUrl + ASIG, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<AsignacionResponse> listarAsignacionesFallback(Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "crearAsignacionFallback")
    public AsignacionResponse crearAsignacion(AsignacionRequest request) {
        HttpEntity<AsignacionRequest> entity = new HttpEntity<>(request, jsonHeaders());
        ResponseEntity<AsignacionResponse> resp = restTemplate.postForEntity(
                baseUrl + ASIG, entity, AsignacionResponse.class);
        return resp.getBody();
    }

    private AsignacionResponse crearAsignacionFallback(AsignacionRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "desactivarAsignacionFallback")
    public void desactivarAsignacion(Long id) {
        restTemplate.delete(baseUrl + ASIG + "/" + id);
    }

    private void desactivarAsignacionFallback(Long id, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    @CircuitBreaker(name = "msRecursos", fallbackMethod = "obtenerResumenFallback")
    public ResumenRecursosResponse obtenerResumen() {
        return restTemplate.getForObject(baseUrl + ASIG + "/resumen", ResumenRecursosResponse.class);
    }

    private ResumenRecursosResponse obtenerResumenFallback(Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-recursos", ex);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
