package cl.duoc.innovatech.ms_mensajeria.config;

import cl.duoc.innovatech.ms_mensajeria.model.Mensaje;
import cl.duoc.innovatech.ms_mensajeria.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MensajeriaDataSeeder implements CommandLineRunner {

    private final MensajeRepository mensajeRepository;

    @Override
    public void run(String... args) {
        if (mensajeRepository.count() > 0) {
            log.info("La base de datos de mensajería ya contiene datos. Se omite el seed.");
            return;
        }

        List<Mensaje> mensajes = List.of(
                Mensaje.builder()
                        .remitenteEmail("karla@innovatech.cl")
                        .remitenteNombre("Karla Herrera")
                        .destinatarioEmail("bryan@innovatech.cl")
                        .destinatarioNombre("Bryan Muñoz")
                        .contenido("Hola Bryan, ¿cómo va el avance de la conciliación bancaria?")
                        .fechaEnvio(LocalDateTime.now().minusHours(5))
                        .leido(true)
                        .build(),
                Mensaje.builder()
                        .remitenteEmail("bryan@innovatech.cl")
                        .remitenteNombre("Bryan Muñoz")
                        .destinatarioEmail("karla@innovatech.cl")
                        .destinatarioNombre("Karla Herrera")
                        .contenido("Vamos avanzados, debería quedar lista para pruebas mañana.")
                        .fechaEnvio(LocalDateTime.now().minusHours(4).minusMinutes(45))
                        .leido(true)
                        .build(),
                Mensaje.builder()
                        .remitenteEmail("karla@innovatech.cl")
                        .remitenteNombre("Karla Herrera")
                        .destinatarioEmail("bryan@innovatech.cl")
                        .destinatarioNombre("Bryan Muñoz")
                        .contenido("Perfecto, avísame cuando esté lista para revisarla.")
                        .fechaEnvio(LocalDateTime.now().minusHours(4).minusMinutes(30))
                        .leido(false)
                        .build(),
                Mensaje.builder()
                        .remitenteEmail("carlos@innovatech.cl")
                        .remitenteNombre("Carlos Soto")
                        .destinatarioEmail("karla@innovatech.cl")
                        .destinatarioNombre("Karla Herrera")
                        .contenido("Karla, ya levanté el inventario de servidores para la migración cloud.")
                        .fechaEnvio(LocalDateTime.now().minusHours(3))
                        .leido(true)
                        .build(),
                Mensaje.builder()
                        .remitenteEmail("karla@innovatech.cl")
                        .remitenteNombre("Karla Herrera")
                        .destinatarioEmail("carlos@innovatech.cl")
                        .destinatarioNombre("Carlos Soto")
                        .contenido("Genial, agenda una reunión esta semana para revisarlo juntos.")
                        .fechaEnvio(LocalDateTime.now().minusHours(2).minusMinutes(50))
                        .leido(false)
                        .build(),
                Mensaje.builder()
                        .remitenteEmail("carlos@innovatech.cl")
                        .remitenteNombre("Carlos Soto")
                        .destinatarioEmail("bryan@innovatech.cl")
                        .destinatarioNombre("Bryan Muñoz")
                        .contenido("Bryan, ¿tienes disponibilidad para apoyar en la migración la próxima semana?")
                        .fechaEnvio(LocalDateTime.now().minusMinutes(45))
                        .leido(false)
                        .build()
        );

        mensajeRepository.saveAll(mensajes);
        log.info("Seed de mensajería creado: {} mensajes.", mensajes.size());
    }
}
