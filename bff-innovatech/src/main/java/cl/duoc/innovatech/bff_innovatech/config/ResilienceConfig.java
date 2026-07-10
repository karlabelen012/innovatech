package cl.duoc.innovatech.bff_innovatech.config;

import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Excluye RecursoNoEncontradoException (404 de negocio) del conteo de fallos
 * de cada Circuit Breaker: que un proyecto/empleado/tarea no exista no significa
 * que el microservicio esté caído, así que no debe contribuir a abrir el circuito.
 */
@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerConfigCustomizer defaultConfigCustomizer() {
        return CircuitBreakerConfigCustomizer.of("default", ResilienceConfig::ignoreRecursoNoEncontrado);
    }

    private static void ignoreRecursoNoEncontrado(CircuitBreakerConfig.Builder builder) {
        builder.ignoreExceptions(RecursoNoEncontradoException.class);
    }
}
