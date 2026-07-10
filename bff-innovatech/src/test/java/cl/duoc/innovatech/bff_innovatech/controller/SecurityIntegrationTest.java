package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica la cadena de seguridad real (SecurityConfig + JwtAuthFilter), a
 * diferencia de los demas *ControllerTest que la desactivan con
 * addFilters=false para probar solo la logica del controller.
 */
@WebMvcTest(controllers = {AuthController.class, CircuitBreakerStatusController.class})
@Import(SecurityConfig.class)
@DisplayName("Seguridad JWT del BFF - Pruebas de integración (MockMvc)")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        when(circuitBreakerRegistry.getAllCircuitBreakers())
                .thenReturn(Set.of(CircuitBreaker.ofDefaults("msProyectos")));
    }

    @Test
    @DisplayName("POST /api/auth/login - credenciales válidas retorna 200 con un JWT")
    void login_conCredencialesValidas_retorna200ConToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@innovatech.cl\",\"password\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@innovatech.cl"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/login - contraseña incorrecta retorna 401")
    void login_conPasswordIncorrecta_retorna401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@innovatech.cl\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - email inexistente retorna 401")
    void login_conEmailInexistente_retorna401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"no-existe@innovatech.cl\",\"password\":\"1234\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/bff/circuit-breakers sin token - retorna 401")
    void endpointProtegido_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/bff/circuit-breakers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/bff/circuit-breakers con token inválido - retorna 401")
    void endpointProtegido_conTokenInvalido_retorna401() throws Exception {
        mockMvc.perform(get("/api/bff/circuit-breakers")
                        .header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/bff/circuit-breakers con token válido (obtenido del login) - retorna 200")
    void endpointProtegido_conTokenValido_retorna200() throws Exception {
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"karla@innovatech.cl\",\"password\":\"1234\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginResponse).get("token").asText();

        mockMvc.perform(get("/api/bff/circuit-breakers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("msProyectos"));
    }
}
