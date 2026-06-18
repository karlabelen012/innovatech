package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.exception.MicroservicioNoDisponibleException;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente HTTP del BFF hacia ms-recursos (puerto 8082).
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

    public List<EmpleadoResponse> listarEmpleados() {
        try {
            ResponseEntity<List<EmpleadoResponse>> resp = restTemplate.exchange(
                    baseUrl + EMP, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public EmpleadoResponse obtenerEmpleadoPorId(Long id) {
        try {
            return restTemplate.getForObject(baseUrl + EMP + "/" + id, EmpleadoResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Empleado no encontrado: " + id);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {
        try {
            HttpEntity<EmpleadoRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<EmpleadoResponse> resp = restTemplate.postForEntity(
                    baseUrl + EMP, entity, EmpleadoResponse.class);
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public EmpleadoResponse actualizarEmpleado(Long id, EmpleadoRequest request) {
        try {
            HttpEntity<EmpleadoRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<EmpleadoResponse> resp = restTemplate.exchange(
                    baseUrl + EMP + "/" + id,
                    HttpMethod.PUT, entity, EmpleadoResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Empleado no encontrado: " + id);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public EmpleadoResponse actualizarDisponibilidad(Long id, String disponibilidad) {
        try {
            ResponseEntity<EmpleadoResponse> resp = restTemplate.exchange(
                    baseUrl + EMP + "/" + id + "?disponibilidad=" + disponibilidad,
                    HttpMethod.PATCH, null, EmpleadoResponse.class);
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public void eliminarEmpleado(Long id) {
        try {
            restTemplate.delete(baseUrl + EMP + "/" + id);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Empleado no encontrado: " + id);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    // ── ASIGNACIONES ───────────────────────────────────────────────────────────

    public List<AsignacionResponse> listarAsignaciones() {
        try {
            ResponseEntity<List<AsignacionResponse>> resp = restTemplate.exchange(
                    baseUrl + ASIG, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public AsignacionResponse crearAsignacion(AsignacionRequest request) {
        try {
            HttpEntity<AsignacionRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<AsignacionResponse> resp = restTemplate.postForEntity(
                    baseUrl + ASIG, entity, AsignacionResponse.class);
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public void desactivarAsignacion(Long id) {
        try {
            restTemplate.delete(baseUrl + ASIG + "/" + id);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    public ResumenRecursosResponse obtenerResumen() {
        try {
            return restTemplate.getForObject(baseUrl + ASIG + "/resumen", ResumenRecursosResponse.class);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-recursos", ex);
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
