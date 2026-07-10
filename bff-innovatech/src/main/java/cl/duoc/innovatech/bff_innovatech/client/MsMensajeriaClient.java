package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.MensajeRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ConversacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.MensajeResponse;
import cl.duoc.innovatech.bff_innovatech.exception.MicroservicioNoDisponibleException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP del BFF hacia ms-mensajeria (puerto 8085).
 * Protegido por Circuit Breaker (Resilience4j): si ms-mensajeria cae,
 * el chat falla rápido sin afectar a los demás servicios (proyectos, recursos, analítica).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MsMensajeriaClient {

    private final RestTemplate restTemplate;

    @Value("${ms.mensajeria.url}")
    private String baseUrl;

    private static final String BASE_PATH = "/api/v1/mensajes";

    @CircuitBreaker(name = "msMensajeria", fallbackMethod = "enviarFallback")
    public MensajeResponse enviar(MensajeRequest request) {
        HttpEntity<MensajeRequest> entity = new HttpEntity<>(request, jsonHeaders());
        ResponseEntity<MensajeResponse> resp = restTemplate.postForEntity(
                baseUrl + BASE_PATH, entity, MensajeResponse.class);
        return resp.getBody();
    }

    private MensajeResponse enviarFallback(MensajeRequest request, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-mensajeria", ex);
    }

    @CircuitBreaker(name = "msMensajeria", fallbackMethod = "obtenerConversacionFallback")
    public List<MensajeResponse> obtenerConversacion(String email1, String email2) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + BASE_PATH + "/conversacion")
                .queryParam("email1", email1)
                .queryParam("email2", email2)
                .toUriString();
        ResponseEntity<List<MensajeResponse>> resp = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<MensajeResponse> obtenerConversacionFallback(String email1, String email2, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-mensajeria", ex);
    }

    @CircuitBreaker(name = "msMensajeria", fallbackMethod = "obtenerInboxFallback")
    public List<ConversacionResponse> obtenerInbox(String email) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + BASE_PATH + "/inbox")
                .queryParam("email", email)
                .toUriString();
        ResponseEntity<List<ConversacionResponse>> resp = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<ConversacionResponse> obtenerInboxFallback(String email, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-mensajeria", ex);
    }

    @CircuitBreaker(name = "msMensajeria", fallbackMethod = "marcarComoLeidosFallback")
    public int marcarComoLeidos(String destinatarioEmail, String remitenteEmail) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + BASE_PATH + "/marcar-leidos")
                .queryParam("destinatario", destinatarioEmail)
                .queryParam("remitente", remitenteEmail)
                .toUriString();
        ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                url, HttpMethod.PATCH, null, new ParameterizedTypeReference<>() {});
        Map<String, Object> body = resp.getBody();
        Object actualizados = body != null ? body.get("actualizados") : null;
        return actualizados instanceof Number ? ((Number) actualizados).intValue() : 0;
    }

    private int marcarComoLeidosFallback(String destinatarioEmail, String remitenteEmail, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-mensajeria", ex);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
