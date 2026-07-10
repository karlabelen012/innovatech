package cl.duoc.innovatech.bff_innovatech.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Expone el estado en vivo de cada Circuit Breaker (CLOSED/OPEN/HALF_OPEN)
 * hacia cada microservicio. Útil para demostrar que si un microservicio cae,
 * su circuito se abre de forma independiente sin afectar a los demás.
 */
@RestController
@RequestMapping("/api/bff/circuit-breakers")
@RequiredArgsConstructor
@Tag(name = "BFF – Resiliencia", description = "Estado de los Circuit Breakers hacia cada microservicio")
@CrossOrigin(origins = "*")
public class CircuitBreakerStatusController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping
    @Operation(summary = "Listar el estado actual de todos los circuit breakers")
    public ResponseEntity<List<Map<String, Object>>> estado() {
        List<Map<String, Object>> resultado = circuitBreakerRegistry.getAllCircuitBreakers().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    private Map<String, Object> toMap(CircuitBreaker cb) {
        CircuitBreaker.Metrics metrics = cb.getMetrics();
        return Map.of(
                "nombre", cb.getName(),
                "estado", cb.getState().name(),
                "tasaFallos", metrics.getFailureRate(),
                "llamadasFallidas", metrics.getNumberOfFailedCalls(),
                "llamadasExitosas", metrics.getNumberOfSuccessfulCalls()
        );
    }
}
