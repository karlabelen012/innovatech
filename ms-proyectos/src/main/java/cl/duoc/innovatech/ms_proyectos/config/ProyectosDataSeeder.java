package cl.duoc.innovatech.ms_proyectos.config;

import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import cl.duoc.innovatech.ms_proyectos.model.Tarea;
import cl.duoc.innovatech.ms_proyectos.repository.ProyectoRepository;
import cl.duoc.innovatech.ms_proyectos.repository.TareaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProyectosDataSeeder implements CommandLineRunner {

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;

    @Override
    public void run(String... args) {
        if (proyectoRepository.count() > 0 || tareaRepository.count() > 0) {
            log.info("La base de datos de proyectos ya contiene datos. Se omite el seed.");
            return;
        }

        List<Proyecto> proyectos = List.of(
                Proyecto.builder()
                        .nombre("Portal Financiero")
                        .descripcion("Portal web para gestión de operaciones financieras del cliente")
                        .estado("EN_PROGRESO")
                        .avance(78)
                        .responsable("Bryan Muñoz")
                        .build(),
                Proyecto.builder()
                        .nombre("Plataforma de Operaciones")
                        .descripcion("Plataforma interna para seguimiento de operaciones diarias")
                        .estado("FINALIZADO")
                        .avance(100)
                        .responsable("Karla Herrera")
                        .build(),
                Proyecto.builder()
                        .nombre("Migración Cloud")
                        .descripcion("Migración de infraestructura on-premise a la nube")
                        .estado("PENDIENTE")
                        .avance(0)
                        .responsable("Carlos Soto")
                        .build()
        );

        List<Proyecto> proyectosGuardados = proyectoRepository.saveAll(proyectos);

        Long portalFinancieroId = proyectosGuardados.get(0).getId();
        Long plataformaOperacionesId = proyectosGuardados.get(1).getId();
        Long migracionCloudId = proyectosGuardados.get(2).getId();

        List<Tarea> tareas = List.of(
                Tarea.builder()
                        .titulo("Diseñar modelo de datos de transacciones")
                        .descripcion("Definir esquema de base de datos para movimientos financieros")
                        .estado("FINALIZADO")
                        .responsable("Bryan Muñoz")
                        .proyectoId(portalFinancieroId)
                        .build(),
                Tarea.builder()
                        .titulo("Implementar API de conciliación bancaria")
                        .descripcion("Endpoint para conciliar movimientos con el banco")
                        .estado("EN_PROGRESO")
                        .responsable("Bryan Muñoz")
                        .proyectoId(portalFinancieroId)
                        .build(),
                Tarea.builder()
                        .titulo("Pruebas de integración con pasarela de pago")
                        .descripcion("Validar flujo completo de pagos")
                        .estado("PENDIENTE")
                        .responsable("Bryan Muñoz")
                        .proyectoId(portalFinancieroId)
                        .build(),
                Tarea.builder()
                        .titulo("Desplegar plataforma en ambiente productivo")
                        .descripcion("Despliegue final y validación con el cliente")
                        .estado("FINALIZADO")
                        .responsable("Karla Herrera")
                        .proyectoId(plataformaOperacionesId)
                        .build(),
                Tarea.builder()
                        .titulo("Documentar arquitectura final")
                        .descripcion("Documentación técnica para mantenimiento futuro")
                        .estado("FINALIZADO")
                        .responsable("Karla Herrera")
                        .proyectoId(plataformaOperacionesId)
                        .build(),
                Tarea.builder()
                        .titulo("Levantar inventario de servidores actuales")
                        .descripcion("Relevamiento de la infraestructura on-premise existente")
                        .estado("PENDIENTE")
                        .responsable("Carlos Soto")
                        .proyectoId(migracionCloudId)
                        .build()
        );

        tareaRepository.saveAll(tareas);
        log.info("Seed de proyectos creado: {} proyectos y {} tareas.", proyectosGuardados.size(), tareas.size());
    }
}
