package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.client.MsProyectosClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.bff_innovatech.service.impl.ProyectoBffServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProyectoBffServiceImpl - Pruebas Unitarias")
class ProyectoBffServiceImplTest {

    @Mock
    private MsProyectosClient msProyectosClient;

    @InjectMocks
    private ProyectoBffServiceImpl service;

    private ProyectoResponse proyectoMock;
    private ProyectoRequest  proyectoRequest;

    @BeforeEach
    void setUp() {
        proyectoMock = ProyectoResponse.builder()
                .id(1L)
                .nombre("Sistema CRM")
                .descripcion("Proyecto CRM para cliente fintech")
                .estado("EN_PROGRESO")
                .avance(45)
                .responsable("Bryan Muñoz")
                .build();

        proyectoRequest = ProyectoRequest.builder()
                .nombre("Sistema CRM")
                .descripcion("Proyecto CRM para cliente fintech")
                .estado("EN_PROGRESO")
                .avance(45)
                .responsable("Bryan Muñoz")
                .build();
    }

    // ── LISTAR ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listar() - devuelve lista completa de proyectos")
    void listar_debeRetornarListaProyectos() {
        when(msProyectosClient.listarProyectos()).thenReturn(List.of(proyectoMock));

        List<ProyectoResponse> resultado = service.listar();

        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Sistema CRM");
        verify(msProyectosClient, times(1)).listarProyectos();
    }

    @Test
    @DisplayName("listar() - lista vacía cuando no hay proyectos")
    void listar_debeRetornarListaVacia() {
        when(msProyectosClient.listarProyectos()).thenReturn(List.of());

        List<ProyectoResponse> resultado = service.listar();

        assertThat(resultado).isEmpty();
        verify(msProyectosClient, times(1)).listarProyectos();
    }

    // ── LISTAR POR ESTADO ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorEstado() - filtra proyectos EN_PROGRESO")
    void listarPorEstado_debeRetornarProyectosFiltrados() {
        when(msProyectosClient.listarPorEstado("EN_PROGRESO")).thenReturn(List.of(proyectoMock));

        List<ProyectoResponse> resultado = service.listarPorEstado("EN_PROGRESO");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("EN_PROGRESO");
        verify(msProyectosClient).listarPorEstado("EN_PROGRESO");
    }

    // ── OBTENER POR ID ────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId() - retorna proyecto existente")
    void obtenerPorId_debeRetornarProyecto() {
        when(msProyectosClient.obtenerProyectoPorId(1L)).thenReturn(proyectoMock);

        ProyectoResponse resultado = service.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Sistema CRM");
    }

    @Test
    @DisplayName("obtenerPorId() - lanza excepción si no existe")
    void obtenerPorId_lanzaExcepcionSiNoExiste() {
        when(msProyectosClient.obtenerProyectoPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Proyecto no encontrado: 99"));

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    // ── CREAR ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear() - retorna proyecto creado")
    void crear_debeRetornarProyectoCreado() {
        when(msProyectosClient.crearProyecto(proyectoRequest)).thenReturn(proyectoMock);

        ProyectoResponse resultado = service.crear(proyectoRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Sistema CRM");
        verify(msProyectosClient, times(1)).crearProyecto(proyectoRequest);
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar() - retorna proyecto actualizado")
    void actualizar_debeRetornarProyectoActualizado() {
        ProyectoResponse actualizado = ProyectoResponse.builder()
                .id(1L).nombre("Sistema CRM v2").estado("FINALIZADO").avance(100).build();
        when(msProyectosClient.actualizarProyecto(eq(1L), any())).thenReturn(actualizado);

        ProyectoResponse resultado = service.actualizar(1L, proyectoRequest);

        assertThat(resultado.getAvance()).isEqualTo(100);
        assertThat(resultado.getEstado()).isEqualTo("FINALIZADO");
    }

    // ── ELIMINAR ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar() - delega correctamente al cliente")
    void eliminar_debeInvocarClienteEliminar() {
        doNothing().when(msProyectosClient).eliminarProyecto(1L);

        assertThatCode(() -> service.eliminar(1L)).doesNotThrowAnyException();

        verify(msProyectosClient, times(1)).eliminarProyecto(1L);
    }
}
