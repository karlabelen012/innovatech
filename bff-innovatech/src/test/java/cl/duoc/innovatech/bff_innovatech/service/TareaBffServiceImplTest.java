package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.client.MsProyectosClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.TareaRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.TareaResponse;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.bff_innovatech.service.impl.TareaBffServiceImpl;
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
@DisplayName("TareaBffServiceImpl - Pruebas Unitarias")
class TareaBffServiceImplTest {

    @Mock
    private MsProyectosClient msProyectosClient;

    @InjectMocks
    private TareaBffServiceImpl service;

    private TareaResponse tareaMock;
    private TareaRequest tareaRequest;

    @BeforeEach
    void setUp() {
        tareaMock = TareaResponse.builder()
                .id(1L).titulo("Diseñar base de datos").descripcion("Modelo ER")
                .estado("PENDIENTE").responsable("Bryan Muñoz").proyectoId(1L).build();

        tareaRequest = TareaRequest.builder()
                .titulo("Diseñar base de datos").descripcion("Modelo ER")
                .estado("PENDIENTE").responsable("Bryan Muñoz").proyectoId(1L).build();
    }

    @Test
    @DisplayName("listarPorProyecto() - retorna tareas del proyecto")
    void listarPorProyecto_debeRetornarTareas() {
        when(msProyectosClient.listarTareasPorProyecto(1L)).thenReturn(List.of(tareaMock));

        List<TareaResponse> resultado = service.listarPorProyecto(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProyectoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId() - retorna tarea existente")
    void obtenerPorId_debeRetornarTarea() {
        when(msProyectosClient.obtenerTareaPorId(1L)).thenReturn(tareaMock);

        TareaResponse resultado = service.obtenerPorId(1L);

        assertThat(resultado.getTitulo()).isEqualTo("Diseñar base de datos");
    }

    @Test
    @DisplayName("obtenerPorId() - lanza excepción si no existe")
    void obtenerPorId_lanzaExcepcionSiNoExiste() {
        when(msProyectosClient.obtenerTareaPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Tarea no encontrada: 99"));

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crear() - retorna tarea creada")
    void crear_debeRetornarTareaCreada() {
        when(msProyectosClient.crearTarea(tareaRequest)).thenReturn(tareaMock);

        TareaResponse resultado = service.crear(tareaRequest);

        assertThat(resultado.getTitulo()).isEqualTo("Diseñar base de datos");
        verify(msProyectosClient).crearTarea(tareaRequest);
    }

    @Test
    @DisplayName("actualizar() - retorna tarea actualizada")
    void actualizar_debeRetornarTareaActualizada() {
        TareaResponse actualizada = TareaResponse.builder().id(1L).titulo("Implementar API").estado("EN_PROGRESO").build();
        when(msProyectosClient.actualizarTarea(eq(1L), any())).thenReturn(actualizada);

        TareaResponse resultado = service.actualizar(1L, tareaRequest);

        assertThat(resultado.getEstado()).isEqualTo("EN_PROGRESO");
    }

    @Test
    @DisplayName("eliminar() - delega correctamente al cliente")
    void eliminar_debeInvocarCliente() {
        doNothing().when(msProyectosClient).eliminarTarea(1L);

        assertThatCode(() -> service.eliminar(1L)).doesNotThrowAnyException();

        verify(msProyectosClient, times(1)).eliminarTarea(1L);
    }
}
