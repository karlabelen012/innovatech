package cl.duoc.innovatech.bff_innovatech.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CircuitBreakerStatusController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CircuitBreakerStatusController - Pruebas Unitarias (MockMvc)")
class CircuitBreakerStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        CircuitBreaker cb = CircuitBreaker.ofDefaults("msProyectos");
        when(circuitBreakerRegistry.getAllCircuitBreakers())
                .thenReturn(Set.of(cb));
    }

    @Test
    @DisplayName("GET /api/bff/circuit-breakers - retorna 200 con el estado de cada circuito")
    void estado_retorna200() throws Exception {
        mockMvc.perform(get("/api/bff/circuit-breakers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("msProyectos"))
                .andExpect(jsonPath("$[0].estado").value("CLOSED"));
    }
}
