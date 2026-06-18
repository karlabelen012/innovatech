package cl.duoc.innovatech.ms_recursos.repository;

import cl.duoc.innovatech.ms_recursos.model.Empleado;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de integración - EmpleadoRepository")
class EmpleadoRepositoryTest {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();

        Empleado e1 = Empleado.builder()
                .nombre("Bryan").apellido("Muñoz").email("bryan@test.cl")
                .telefono("+56911111111").rol(RolEmpleado.DESARROLLADOR)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(40).habilidades("Java, React")
                .fechaIngreso(LocalDate.now()).activo(true).build();

        Empleado e2 = Empleado.builder()
                .nombre("Karla").apellido("Herrera").email("karla@test.cl")
                .telefono("+56922222222").rol(RolEmpleado.ARQUITECTO)
                .disponibilidad(DisponibilidadEstado.OCUPADO)
                .horasSemanales(40).habilidades("AWS, Docker")
                .fechaIngreso(LocalDate.now()).activo(true).build();

        Empleado e3 = Empleado.builder()
                .nombre("Pedro").apellido("González").email("pedro@test.cl")
                .telefono("+56933333333").rol(RolEmpleado.QA)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(40).habilidades("Testing, Selenium")
                .fechaIngreso(LocalDate.now()).activo(false).build();

        empleadoRepository.saveAll(List.of(e1, e2, e3));
    }

    @Test
    @DisplayName("Guardar y encontrar empleado por email")
    void findByEmail_retornaEmpleado() {
        Optional<Empleado> resultado = empleadoRepository.findByEmail("bryan@test.cl");
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Bryan");
    }

    @Test
    @DisplayName("Listar solo empleados activos")
    void findByActivoTrue_soloActivos() {
        List<Empleado> activos = empleadoRepository.findByActivoTrue();
        assertThat(activos).hasSize(2);
        assertThat(activos).allMatch(Empleado::getActivo);
    }

    @Test
    @DisplayName("Filtrar por disponibilidad DISPONIBLE")
    void findByDisponibilidad_disponibles() {
        List<Empleado> disponibles = empleadoRepository.findByDisponibilidadAndActivoTrue(DisponibilidadEstado.DISPONIBLE);
        assertThat(disponibles).hasSize(1);
        assertThat(disponibles.get(0).getNombre()).isEqualTo("Bryan");
    }

    @Test
    @DisplayName("Filtrar por rol ARQUITECTO")
    void findByRol_arquitectos() {
        List<Empleado> arquitectos = empleadoRepository.findByRolAndActivoTrue(RolEmpleado.ARQUITECTO);
        assertThat(arquitectos).hasSize(1);
        assertThat(arquitectos.get(0).getNombre()).isEqualTo("Karla");
    }

    @Test
    @DisplayName("Verificar existencia por email")
    void existsByEmail_existente() {
        assertThat(empleadoRepository.existsByEmail("bryan@test.cl")).isTrue();
        assertThat(empleadoRepository.existsByEmail("noexiste@test.cl")).isFalse();
    }

    @Test
    @DisplayName("Contar por disponibilidad")
    void countByDisponibilidad() {
        long disponibles = empleadoRepository.countByDisponibilidad(DisponibilidadEstado.DISPONIBLE);
        assertThat(disponibles).isEqualTo(2); // Bryan activo + Pedro inactivo
    }

    @Test
    @DisplayName("Buscar por habilidad usando JPQL")
    void findByHabilidad_retornaCoincidencias() {
        List<Empleado> javaDevs = empleadoRepository.findByHabilidad("Java");
        assertThat(javaDevs).hasSize(1);
        assertThat(javaDevs.get(0).getEmail()).isEqualTo("bryan@test.cl");
    }
}
