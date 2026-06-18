package cl.duoc.innovatech.bff_innovatech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BFF – Backend for Frontend de Innovatech Solutions.
 * Actúa como capa intermedia entre el frontend (React) y los tres
 * microservicios: ms-proyectos (8081), ms-recursos (8082), ms-analitica (8083).
 * Puerto propio: 8080.
 */
@SpringBootApplication
public class BffInnovatechApplication {
    public static void main(String[] args) {
        SpringApplication.run(BffInnovatechApplication.class, args);
    }
}
