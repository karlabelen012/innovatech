package cl.duoc.innovatech.ms_recursos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Test de carga del contexto de la aplicación")
class MsRecursosApplicationTests {

    @Test
    @DisplayName("El contexto de la aplicación carga correctamente")
    void contextLoads() {
        // Verifica que Spring Boot inicia sin errores
    }
}
