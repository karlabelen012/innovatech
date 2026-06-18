package cl.duoc.innovatech.ms_recursos.service;

import cl.duoc.innovatech.ms_recursos.dto.EmpleadoDTO;
import cl.duoc.innovatech.ms_recursos.exception.EmailDuplicadoException;
import cl.duoc.innovatech.ms_recursos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_recursos.model.Empleado;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import cl.duoc.innovatech.ms_recursos.repository.EmpleadoRepository;
import cl.duoc.innovatech.ms_recursos.service.impl.EmpleadoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - EmpleadoService")
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    private Empleado empleadoBase;
    private EmpleadoDTO empleadoDTOBase;

    @BeforeEach
    void setUp() {
        empleadoBase = Empleado.builder()
                .id(1L)
                .nombre("Bryan")
                .apellido("Muñoz")
                .email("bryan@innovatech.cl")
                .telefono("+56912345678")
                .rol(RolEmpleado.DESARROLLADOR)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(40)
                .habilidades("Java, Spring Boot, React")
                .fechaIngreso(LocalDate.now())
                .activo(true)
                .build();

        empleadoDTOBase = EmpleadoDTO.builder()
                .nombre("Bryan")
                .apellido("Muñoz")
                .email("bryan@innovatech.cl")
                .telefono("+56912345678")
                .rol(RolEmpleado.DESARROLLADOR)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(40)
                .habilidades("Java, Spring Boot, React")
                .build();
    }

    // ===================== CREAR =====================

    @Test
    @DisplayName("Crear empleado exitosamente")
    void crearEmpleado_exitoso() {
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoBase);

        EmpleadoDTO resultado = empleadoService.crear(empleadoDTOBase);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Bryan");
        assertThat(resultado.getEmail()).isEqualTo("bryan@innovatech.cl");
        assertThat(resultado.getRol()).isEqualTo(RolEmpleado.DESARROLLADOR);
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    @DisplayName("Crear empleado con email duplicado lanza excepción")
    void crearEmpleado_emailDuplicado_lanzaExcepcion() {
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> empleadoService.crear(empleadoDTOBase))
                .isInstanceOf(EmailDuplicadoException.class)
                .hasMessageContaining("bryan@innovatech.cl");

        verify(empleadoRepository, never()).save(any());
    }

    // ===================== OBTENER =====================

    @Test
    @DisplayName("Obtener empleado por ID existente")
    void obtenerPorId_existe_retornaDTO() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoBase));

        EmpleadoDTO resultado = empleadoService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Bryan");
    }

    @Test
    @DisplayName("Obtener empleado por ID inexistente lanza excepción")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empleadoService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Obtener todos los empleados")
    void obtenerTodos_retornaLista() {
        Empleado e2 = Empleado.builder().id(2L).nombre("Karla").apellido("Herrera")
                .email("karla@innovatech.cl").rol(RolEmpleado.ARQUITECTO)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE).activo(true).build();

        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(empleadoBase, e2));

        List<EmpleadoDTO> resultado = empleadoService.obtenerTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Bryan");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Karla");
    }

    @Test
    @DisplayName("Obtener empleados activos")
    void obtenerActivos_retornaActivos() {
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of(empleadoBase));

        List<EmpleadoDTO> resultado = empleadoService.obtenerActivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
    }

    // ===================== ACTUALIZAR =====================

    @Test
    @DisplayName("Actualizar empleado exitosamente")
    void actualizar_exitoso() {
        EmpleadoDTO dtoActualizado = EmpleadoDTO.builder()
                .nombre("Bryan Updated")
                .apellido("Muñoz")
                .email("bryan@innovatech.cl")
                .telefono("+56987654321")
                .rol(RolEmpleado.ARQUITECTO)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(40)
                .build();

        Empleado actualizado = Empleado.builder().id(1L).nombre("Bryan Updated")
                .apellido("Muñoz").email("bryan@innovatech.cl")
                .rol(RolEmpleado.ARQUITECTO).disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .activo(true).build();

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoBase));
        when(empleadoRepository.save(any())).thenReturn(actualizado);

        EmpleadoDTO resultado = empleadoService.actualizar(1L, dtoActualizado);

        assertThat(resultado.getNombre()).isEqualTo("Bryan Updated");
        assertThat(resultado.getRol()).isEqualTo(RolEmpleado.ARQUITECTO);
    }

    @Test
    @DisplayName("Actualizar empleado inexistente lanza excepción")
    void actualizar_noExiste_lanzaExcepcion() {
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empleadoService.actualizar(99L, empleadoDTOBase))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    // ===================== ELIMINAR =====================

    @Test
    @DisplayName("Eliminar empleado (baja lógica)")
    void eliminar_exitoso_desactivaEmpleado() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoBase));
        when(empleadoRepository.save(any())).thenReturn(empleadoBase);

        empleadoService.eliminar(1L);

        verify(empleadoRepository).save(argThat(e -> !e.getActivo()));
    }

    @Test
    @DisplayName("Eliminar empleado inexistente lanza excepción")
    void eliminar_noExiste_lanzaExcepcion() {
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empleadoService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    // ===================== FILTROS =====================

    @Test
    @DisplayName("Filtrar por disponibilidad DISPONIBLE")
    void obtenerPorDisponibilidad_retornaFiltrados() {
        when(empleadoRepository.findByDisponibilidadAndActivoTrue(DisponibilidadEstado.DISPONIBLE))
                .thenReturn(List.of(empleadoBase));

        List<EmpleadoDTO> resultado = empleadoService.obtenerPorDisponibilidad(DisponibilidadEstado.DISPONIBLE);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDisponibilidad()).isEqualTo(DisponibilidadEstado.DISPONIBLE);
    }

    @Test
    @DisplayName("Filtrar por rol DESARROLLADOR")
    void obtenerPorRol_retornaFiltrados() {
        when(empleadoRepository.findByRolAndActivoTrue(RolEmpleado.DESARROLLADOR))
                .thenReturn(List.of(empleadoBase));

        List<EmpleadoDTO> resultado = empleadoService.obtenerPorRol(RolEmpleado.DESARROLLADOR);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRol()).isEqualTo(RolEmpleado.DESARROLLADOR);
    }

    @Test
    @DisplayName("Actualizar disponibilidad del empleado")
    void actualizarDisponibilidad_exitoso() {
        Empleado actualizado = Empleado.builder().id(1L).nombre("Bryan").apellido("Muñoz")
                .email("bryan@innovatech.cl").rol(RolEmpleado.DESARROLLADOR)
                .disponibilidad(DisponibilidadEstado.VACACIONES).activo(true).build();

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoBase));
        when(empleadoRepository.save(any())).thenReturn(actualizado);

        EmpleadoDTO resultado = empleadoService.actualizarDisponibilidad(1L, DisponibilidadEstado.VACACIONES);

        assertThat(resultado.getDisponibilidad()).isEqualTo(DisponibilidadEstado.VACACIONES);
    }

    @Test
    @DisplayName("Buscar por habilidad")
    void buscarPorHabilidad_retornaResultados() {
        when(empleadoRepository.findByHabilidad("Java")).thenReturn(List.of(empleadoBase));

        List<EmpleadoDTO> resultado = empleadoService.buscarPorHabilidad("Java");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getHabilidades()).contains("Java");
    }
}
