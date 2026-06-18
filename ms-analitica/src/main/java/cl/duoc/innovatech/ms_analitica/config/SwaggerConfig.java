package cl.duoc.innovatech.ms_analitica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Analítica - Innovatech Solutions")
                        .version("1.0.0")
                        .description("Microservicio de Monitoreo y Analítica: dashboard, KPIs y reportes de proyectos"));
    }
}
