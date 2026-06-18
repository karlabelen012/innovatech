package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
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
 * Cliente HTTP del BFF hacia ms-proyectos (puerto 8081).
 * Aplica Circuit Breaker manual: captura ResourceAccessException
 * y lanza MicroservicioNoDisponibleException para evitar propagación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MsProyectosClient {

    private final RestTemplate restTemplate;

    @Value("${ms.proyectos.url}")
    private String baseUrl;

    private static final String BASE_PATH = "/api/v1/proyectos";

    public List<ProyectoResponse> listarProyectos() {
        try {
            log.debug("GET {}{}", baseUrl, BASE_PATH);
            ResponseEntity<List<ProyectoResponse>> resp = restTemplate.exchange(
                    baseUrl + BASE_PATH,
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
        }
    }

    public ProyectoResponse obtenerProyectoPorId(Long id) {
        try {
            return restTemplate.getForObject(baseUrl + BASE_PATH + "/" + id, ProyectoResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado: " + id);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
        }
    }

    public ProyectoResponse crearProyecto(ProyectoRequest request) {
        try {
            HttpEntity<ProyectoRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<ProyectoResponse> resp = restTemplate.postForEntity(
                    baseUrl + BASE_PATH, entity, ProyectoResponse.class);
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
        }
    }

    public ProyectoResponse actualizarProyecto(Long id, ProyectoRequest request) {
        try {
            HttpEntity<ProyectoRequest> entity = new HttpEntity<>(request, jsonHeaders());
            ResponseEntity<ProyectoResponse> resp = restTemplate.exchange(
                    baseUrl + BASE_PATH + "/" + id,
                    HttpMethod.PUT, entity, ProyectoResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado: " + id);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
        }
    }

    public void eliminarProyecto(Long id) {
        try {
            restTemplate.delete(baseUrl + BASE_PATH + "/" + id);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado: " + id);
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
        }
    }

    public List<ProyectoResponse> listarPorEstado(String estado) {
        try {
            ResponseEntity<List<ProyectoResponse>> resp = restTemplate.exchange(
                    baseUrl + BASE_PATH + "?estado=" + estado,
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            throw new MicroservicioNoDisponibleException("ms-proyectos", ex);
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
