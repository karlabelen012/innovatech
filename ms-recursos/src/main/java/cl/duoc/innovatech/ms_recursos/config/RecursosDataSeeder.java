package cl.duoc.innovatech.ms_recursos.config;

import cl.duoc.innovatech.ms_recursos.model.Asignacion;
import cl.duoc.innovatech.ms_recursos.model.Empleado;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import cl.duoc.innovatech.ms_recursos.repository.AsignacionRepository;
import cl.duoc.innovatech.ms_recursos.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecursosDataSeeder implements CommandLineRunner {

    private final EmpleadoRepository empleadoRepository;
    private final AsignacionRepository asignacionRepository;

    @Override
    public void run(String... args) {
        if (empleadoRepository.count() > 0 || asignacionRepository.count() > 0) {
            log.info("La base de datos de recursos ya contiene datos. Se omite el seed.");
            return;
        }

        List<Empleado> empleados = List.of(
                Empleado.builder()
                        .nombre("Bryan")
                        .apellido("Muñoz")
                        .email("bryan@innovatech.cl")
                        .telefono("+56911111111")
                        .rol(RolEmpleado.DESARROLLADOR)
                        .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                        .horasSemanales(40)
                        .habilidades("Java, Spring Boot, React")
                        .fechaIngreso(LocalDate.now().minusDays(120))
                        .activo(true)
                        .build(),
                Empleado.builder()
                        .nombre("Karla")
                        .apellido("Herrera")
                        .email("karla@innovatech.cl")
                        .telefono("+56922222222")
                        .rol(RolEmpleado.ARQUITECTO)
                        .disponibilidad(DisponibilidadEstado.OCUPADO)
                        .horasSemanales(40)
                        .habilidades("Arquitectura, Cloud, Java")
                        .fechaIngreso(LocalDate.now().minusDays(200))
                        .activo(true)
                        .build(),
                Empleado.builder()
                        .nombre("Carlos")
                        .apellido("Soto")
                        .email("carlos@innovatech.cl")
                        .telefono("+56933333333")
                        .rol(RolEmpleado.GESTOR)
                        .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                        .horasSemanales(35)
                        .habilidades("Gestión, Scrum, Producto")
                        .fechaIngreso(LocalDate.now().minusDays(90))
                        .activo(true)
                        .build()
        );

        List<Empleado> empleadosGuardados = empleadoRepository.saveAll(empleados);

        List<Asignacion> asignaciones = List.of(
                Asignacion.builder()
                        .empleado(empleadosGuardados.get(0))
                        .proyectoId(1001L)
                        .nombreProyecto("Portal Financiero")
                        .fechaInicio(LocalDate.now().minusDays(30))
                        .horasAsignadas(20)
                        .rolEnProyecto("Backend")
                        .activo(true)
                        .build(),
                Asignacion.builder()
                        .empleado(empleadosGuardados.get(1))
                        .proyectoId(1002L)
                        .nombreProyecto("Plataforma de Operaciones")
                        .fechaInicio(LocalDate.now().minusDays(15))
                        .horasAsignadas(16)
                        .rolEnProyecto("Arquitecto")
                        .activo(true)
                        .build()
        );

        asignacionRepository.saveAll(asignaciones);
        log.info("Seed de recursos creado: {} empleados y {} asignaciones.", empleadosGuardados.size(), asignaciones.size());
    }
}
