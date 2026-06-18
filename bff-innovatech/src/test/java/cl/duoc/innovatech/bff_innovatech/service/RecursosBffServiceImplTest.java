package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.client.MsRecursosClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.bff_innovatech.service.impl.RecursosBffServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecursosBffServiceImpl - Pruebas Unitarias")
class RecursosBffServiceImplTest {

    @Mock
    private MsRecursosClient msRecursosClient;

    @InjectMocks
    private RecursosBffServiceImpl service;

    private EmpleadoResponse empleadoMock;
    private EmpleadoRequest  empleadoRequest;
    private AsignacionResponse asignacionMock;
    private AsignacionRequest  asignacionRequest;

    @BeforeEach
    void setUp() {
        empleadoMock = EmpleadoResponse.builder()
                .id(1L).nombre("Karla").apellido("Herrera")
                .email("karla@innovatech.cl").rol("DESARROLLADOR")
                .disponibilidad("DISPONIBLE").horasSemanales(40).activo(true)
                .build();

        empleadoRequest = EmpleadoRequest.builder()
                .nombre("Karla").apellido("Herrera")
                .email("karla@innovatech.cl").rol("DESARROLLADOR")
                .disponibilidad("DISPONIBLE").horasSemanales(40)
                .build();

        asignacionMock = AsignacionResponse.builder()
                .id(10L).empleadoId(1L).proyectoId(5L)
                .nombreProyecto("Sistema CRM")
                .fechaInicio(LocalDate.now())
                .horasAsignadas(20).activo(true)
                .build();

        asignacionRequest = AsignacionRequest.builder()
                .empleadoId(1L).proyectoId(5L)
                .nombreProyecto("Sistema CRM")
                .fechaInicio(LocalDate.now())
                .horasAsignadas(20)
                .build();
    }

    // ── EMPLEADOS ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarEmpleados() - devuelve lista de empleados")
    void listarEmpleados_debeRetornarLista() {
        when(msRecursosClient.listarEmpleados()).thenReturn(List.of(empleadoMock));

        List<EmpleadoResponse> resultado = service.listarEmpleados();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("karla@innovatech.cl");
        verify(msRecursosClient).listarEmpleados();
    }

    @Test
    @DisplayName("obtenerEmpleadoPorId() - retorna empleado existente")
    void obtenerEmpleadoPorId_debeRetornarEmpleado() {
        when(msRecursosClient.obtenerEmpleadoPorId(1L)).thenReturn(empleadoMock);

        EmpleadoResponse resultado = service.obtenerEmpleadoPorId(1L);

        assertThat(resultado.getNombre()).isEqualTo("Karla");
    }

    @Test
    @DisplayName("obtenerEmpleadoPorId() - lanza excepción si no existe")
    void obtenerEmpleadoPorId_lanzaExcepcionSiNoExiste() {
        when(msRecursosClient.obtenerEmpleadoPorId(999L))
                .thenThrow(new RecursoNoEncontradoException("Empleado no encontrado: 999"));

        assertThatThrownBy(() -> service.obtenerEmpleadoPorId(999L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crearEmpleado() - retorna empleado creado")
    void crearEmpleado_debeRetornarEmpleadoCreado() {
        when(msRecursosClient.crearEmpleado(empleadoRequest)).thenReturn(empleadoMock);

        EmpleadoResponse resultado = service.crearEmpleado(empleadoRequest);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(msRecursosClient).crearEmpleado(empleadoRequest);
    }

    @Test
    @DisplayName("actualizarEmpleado() - delega actualización al cliente")
    void actualizarEmpleado_debeDelegarAlCliente() {
        when(msRecursosClient.actualizarEmpleado(eq(1L), any())).thenReturn(empleadoMock);

        EmpleadoResponse resultado = service.actualizarEmpleado(1L, empleadoRequest);

        assertThat(resultado).isNotNull();
        verify(msRecursosClient).actualizarEmpleado(eq(1L), any());
    }

    @Test
    @DisplayName("actualizarDisponibilidad() - cambia estado correctamente")
    void actualizarDisponibilidad_debeCambiarEstado() {
        EmpleadoResponse ocupado = EmpleadoResponse.builder()
                .id(1L).disponibilidad("OCUPADO").build();
        when(msRecursosClient.actualizarDisponibilidad(1L, "OCUPADO")).thenReturn(ocupado);

        EmpleadoResponse resultado = service.actualizarDisponibilidad(1L, "OCUPADO");

        assertThat(resultado.getDisponibilidad()).isEqualTo("OCUPADO");
    }

    @Test
    @DisplayName("eliminarEmpleado() - invoca cliente sin errores")
    void eliminarEmpleado_debeInvocarCliente() {
        doNothing().when(msRecursosClient).eliminarEmpleado(1L);

        assertThatCode(() -> service.eliminarEmpleado(1L)).doesNotThrowAnyException();

        verify(msRecursosClient).eliminarEmpleado(1L);
    }

    // ── ASIGNACIONES ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarAsignaciones() - devuelve lista")
    void listarAsignaciones_debeRetornarLista() {
        when(msRecursosClient.listarAsignaciones()).thenReturn(List.of(asignacionMock));

        List<AsignacionResponse> resultado = service.listarAsignaciones();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreProyecto()).isEqualTo("Sistema CRM");
    }

    @Test
    @DisplayName("crearAsignacion() - retorna asignación creada")
    void crearAsignacion_debeRetornarAsignacionCreada() {
        when(msRecursosClient.crearAsignacion(asignacionRequest)).thenReturn(asignacionMock);

        AsignacionResponse resultado = service.crearAsignacion(asignacionRequest);

        assertThat(resultado.getEmpleadoId()).isEqualTo(1L);
        assertThat(resultado.getProyectoId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("desactivarAsignacion() - invoca cliente correctamente")
    void desactivarAsignacion_debeInvocarCliente() {
        doNothing().when(msRecursosClient).desactivarAsignacion(10L);

        assertThatCode(() -> service.desactivarAsignacion(10L)).doesNotThrowAnyException();

        verify(msRecursosClient).desactivarAsignacion(10L);
    }

    // ── RESUMEN ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerResumen() - devuelve totales de recursos")
    void obtenerResumen_debeRetornarTotales() {
        ResumenRecursosResponse resumen = ResumenRecursosResponse.builder()
                .totalEmpleados(10L).empleadosDisponibles(6L)
                .empleadosOcupados(4L).asignacionesActivas(8L)
                .build();
        when(msRecursosClient.obtenerResumen()).thenReturn(resumen);

        ResumenRecursosResponse resultado = service.obtenerResumen();

        assertThat(resultado.getTotalEmpleados()).isEqualTo(10L);
        assertThat(resultado.getEmpleadosDisponibles()).isEqualTo(6L);
    }
}
