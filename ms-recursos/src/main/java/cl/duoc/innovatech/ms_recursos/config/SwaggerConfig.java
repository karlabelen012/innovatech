package cl.duoc.innovatech.ms_recursos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS-Recursos API - Innovatech Solutions")
                        .description("Microservicio de Gestión de Recursos Humanos y Colaboración. " +
                                "Permite gestionar empleados, asignaciones a proyectos, disponibilidad y carga laboral.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bryan Muñoz - Karla Herrera")
                                .email("equipo@innovatech.cl")));
    }
}
