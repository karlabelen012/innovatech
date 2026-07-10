package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import cl.duoc.innovatech.bff_innovatech.exception.MicroservicioNoDisponibleException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente HTTP del BFF hacia ms-analitica (puerto 8083).
 * Protegido por Circuit Breaker (Resilience4j): si ms-analitica cae,
 * el circuito se abre y las llamadas fallan rápido sin afectar a los demás servicios.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MsAnaliticaClient {

    private final RestTemplate restTemplate;

    @Value("${ms.analitica.url}")
    private String baseUrl;

    private static final String DASH = "/api/v1/dashboard";
    private static final String KPI  = "/api/v1/kpis";

    @CircuitBreaker(name = "msAnalitica", fallbackMethod = "obtenerDashboardFallback")
    public DashboardResponse obtenerDashboard() {
        log.debug("GET {}{}", baseUrl, DASH);
        return restTemplate.getForObject(baseUrl + DASH, DashboardResponse.class);
    }

    private DashboardResponse obtenerDashboardFallback(Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-analitica", ex);
    }

    @CircuitBreaker(name = "msAnalitica", fallbackMethod = "listarKpisFallback")
    public List<KpiMetricaResponse> listarKpis() {
        ResponseEntity<List<KpiMetricaResponse>> resp = restTemplate.exchange(
                baseUrl + KPI, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<KpiMetricaResponse> listarKpisFallback(Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-analitica", ex);
    }

    @CircuitBreaker(name = "msAnalitica", fallbackMethod = "listarKpisPorCategoriaFallback")
    public List<KpiMetricaResponse> listarKpisPorCategoria(String categoria) {
        ResponseEntity<List<KpiMetricaResponse>> resp = restTemplate.exchange(
                baseUrl + KPI + "?categoria=" + categoria,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return resp.getBody();
    }

    private List<KpiMetricaResponse> listarKpisPorCategoriaFallback(String categoria, Throwable ex) {
        throw new MicroservicioNoDisponibleException("ms-analitica", ex);
    }
}
