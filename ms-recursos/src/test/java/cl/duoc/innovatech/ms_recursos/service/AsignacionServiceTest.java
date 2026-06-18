package cl.duoc.innovatech.ms_recursos.service;

import cl.duoc.innovatech.ms_recursos.dto.AsignacionDTO;
import cl.duoc.innovatech.ms_recursos.dto.DisponibilidadDTO;
import cl.duoc.innovatech.ms_recursos.dto.ResumenRecursosDTO;
import cl.duoc.innovatech.ms_recursos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_recursos.model.Asignacion;
import cl.duoc.innovatech.ms_recursos.model.Empleado;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import cl.duoc.innovatech.ms_recursos.repository.AsignacionRepository;
import cl.duoc.innovatech.ms_recursos.repository.EmpleadoRepository;
import cl.duoc.innovatech.ms_recursos.service.impl.AsignacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - AsignacionService")
class AsignacionServiceTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private AsignacionServiceImpl asignacionService;

    private Empleado empleado;
    private Asignacion asignacion;
    private AsignacionDTO asignacionDTO;

    @BeforeEach
    void setUp() {
        empleado = Empleado.builder()
                .id(1L).nombre("Bryan").apellido("Muñoz")
                .email("bryan@innovatech.cl").rol(RolEmpleado.DESARROLLADOR)
                .disponibilidad(DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(40).activo(true).build();

        asignacion = Asignacion.builder()
                .id(1L).empleado(empleado).proyectoId(100L)
                .nombreProyecto("Proyecto Alpha").fechaInicio(LocalDate.now())
                .horasAsignadas(20).rolEnProyecto("Dev Backend").activo(true).build();

        asignacionDTO = AsignacionDTO.builder()
                .empleadoId(1L).proyectoId(100L)
                .nombreProyecto("Proyecto Alpha").fechaInicio(LocalDate.now())
                .horasAsignadas(20).rolEnProyecto("Dev Backend").build();
    }

    @Test
    @DisplayName("Crear asignación exitosamente")
    void crear_exitoso() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(asignacionRepository.save(any())).thenReturn(asignacion);
        when(empleadoRepository.save(any())).thenReturn(empleado);

        AsignacionDTO resultado = asignacionService.crear(asignacionDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreProyecto()).isEqualTo("Proyecto Alpha");
        assertThat(resultado.getProyectoId()).isEqualTo(100L);
        verify(empleadoRepository).save(any()); // actualiza disponibilidad a OCUPADO
    }

    @Test
    @DisplayName("Crear asignación con empleado inexistente lanza excepción")
    void crear_empleadoNoExiste_lanzaExcepcion() {
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());
        asignacionDTO.setEmpleadoId(99L);

        assertThatThrownBy(() -> asignacionService.crear(asignacionDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Obtener asignación por ID existente")
    void obtenerPorId_existe() {
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));

        AsignacionDTO resultado = asignacionService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombreProyecto()).isEqualTo("Proyecto Alpha");
    }

    @Test
    @DisplayName("Obtener asignación por ID inexistente lanza excepción")
    void obtenerPorId_noExiste() {
        when(asignacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> asignacionService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Obtener todas las asignaciones")
    void obtenerTodas_retornaLista() {
        when(asignacionRepository.findAll()).thenReturn(List.of(asignacion));

        List<AsignacionDTO> resultado = asignacionService.obtenerTodas();

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Obtener asignaciones por empleado")
    void obtenerPorEmpleado() {
        when(asignacionRepository.findByEmpleadoId(1L)).thenReturn(List.of(asignacion));

        List<AsignacionDTO> resultado = asignacionService.obtenerPorEmpleado(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmpleadoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener asignaciones por proyecto")
    void obtenerPorProyecto() {
        when(asignacionRepository.findByProyectoId(100L)).thenReturn(List.of(asignacion));

        List<AsignacionDTO> resultado = asignacionService.obtenerPorProyecto(100L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProyectoId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Actualizar asignación exitosamente")
    void actualizar_exitoso() {
        AsignacionDTO dtoActualizado = AsignacionDTO.builder()
                .nombreProyecto("Proyecto Beta").fechaInicio(LocalDate.now())
                .horasAsignadas(30).rolEnProyecto("Arquitecto").build();

        Asignacion actualizada = Asignacion.builder().id(1L).empleado(empleado)
                .proyectoId(100L).nombreProyecto("Proyecto Beta")
                .fechaInicio(LocalDate.now()).horasAsignadas(30).activo(true).build();

        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(actualizada);

        AsignacionDTO resultado = asignacionService.actualizar(1L, dtoActualizado);

        assertThat(resultado.getNombreProyecto()).isEqualTo("Proyecto Beta");
        assertThat(resultado.getHorasAsignadas()).isEqualTo(30);
    }

    @Test
    @DisplayName("Desactivar asignación y liberar empleado")
    void desactivar_libraEmpleadoSinAsignaciones() {
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);
        when(asignacionRepository.findByEmpleadoIdAndActivoTrue(1L))
                .thenReturn(Collections.emptyList());
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any())).thenReturn(empleado);

        asignacionService.desactivar(1L);

        verify(empleadoRepository).save(argThat(e ->
                e.getDisponibilidad() == DisponibilidadEstado.DISPONIBLE));
    }

    @Test
    @DisplayName("Desactivar asignación, empleado sigue ocupado si tiene otras")
    void desactivar_empleadoSigueOcupado() {
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any())).thenReturn(asignacion);
        when(asignacionRepository.findByEmpleadoIdAndActivoTrue(1L))
                .thenReturn(List.of(asignacion));

        asignacionService.desactivar(1L);

        verify(empleadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener disponibilidad del equipo")
    void obtenerDisponibilidadEquipo() {
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of(empleado));
        when(asignacionRepository.sumHorasAsignadasByEmpleadoId(1L)).thenReturn(20);

        List<DisponibilidadDTO> resultado = asignacionService.obtenerDisponibilidadEquipo();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getHorasAsignadas()).isEqualTo(20);
        assertThat(resultado.get(0).getHorasLibres()).isEqualTo(20); // 40 - 20
    }

    @Test
    @DisplayName("Obtener resumen de recursos")
    void obtenerResumen_retornaDatos() {
        when(empleadoRepository.countByActivoTrue()).thenReturn(10L);
        when(empleadoRepository.countByDisponibilidad(DisponibilidadEstado.DISPONIBLE)).thenReturn(5L);
        when(empleadoRepository.countByDisponibilidad(DisponibilidadEstado.OCUPADO)).thenReturn(4L);
        when(empleadoRepository.countByDisponibilidad(DisponibilidadEstado.VACACIONES)).thenReturn(1L);
        when(asignacionRepository.countByActivoTrue()).thenReturn(8L);

        ResumenRecursosDTO resumen = asignacionService.obtenerResumen();

        assertThat(resumen.getTotalEmpleados()).isEqualTo(10L);
        assertThat(resumen.getEmpleadosDisponibles()).isEqualTo(5L);
        assertThat(resumen.getEmpleadosOcupados()).isEqualTo(4L);
        assertThat(resumen.getTotalAsignacionesActivas()).isEqualTo(8L);
    }
}
