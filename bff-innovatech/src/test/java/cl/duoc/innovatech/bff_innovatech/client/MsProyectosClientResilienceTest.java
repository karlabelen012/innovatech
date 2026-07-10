package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.exception.MicroservicioNoDisponibleException;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Prueba de integración real del Circuit Breaker (Resilience4j) sobre MsProyectosClient.
 * Demuestra que si ms-proyectos cae (fallos de conexión repetidos) el circuito
 * se abre y deja de llamar al microservicio, y que un 404 normal nunca lo abre.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("MsProyectosClient - Circuit Breaker (Resilience4j)")
class MsProyectosClientResilienceTest {

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private MsProyectosClient client;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void resetCircuit() {
        circuitBreakerRegistry.circuitBreaker("msProyectos").reset();
        reset(restTemplate);
    }

    @Test
    @DisplayName("Fallos de conexión repetidos abren el circuito y activan el fallback")
    void fallosDeConexionRepetidos_abrenElCircuito() {
        when(restTemplate.getForObject(anyString(), eq(ProyectoResponse.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        // minimumNumberOfCalls=3 en application.properties: al tercer fallo (100% de error) el circuito abre
        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(() -> client.obtenerProyectoPorId(1L))
                    .isInstanceOf(MicroservicioNoDisponibleException.class);
        }

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("msProyectos");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // Con el circuito abierto, las siguientes llamadas fallan rápido sin tocar el RestTemplate
        reset(restTemplate);
        assertThatThrownBy(() -> client.obtenerProyectoPorId(1L))
                .isInstanceOf(MicroservicioNoDisponibleException.class);
        verify(restTemplate, never()).getForObject(anyString(), eq(ProyectoResponse.class));
    }

    @Test
    @DisplayName("Un 404 (recurso no encontrado) no cuenta como fallo y mantiene el circuito cerrado")
    void recursoNoEncontrado_mantieneElCircuitoCerrado() {
        when(restTemplate.getForObject(anyString(), eq(ProyectoResponse.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], null));

        assertThatThrownBy(() -> client.obtenerProyectoPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("msProyectos");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }
}
