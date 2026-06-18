package cl.duoc.innovatech.bff_innovatech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BFF — Innovatech Solutions")
                        .version("1.0.0")
                        .description("Backend for Frontend: punto único de entrada hacia ms-proyectos, ms-recursos y ms-analitica.")
                        .contact(new Contact()
                                .name("Equipo Innovatech")
                                .email("devteam@innovatech.cl")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local")));
    }
}
