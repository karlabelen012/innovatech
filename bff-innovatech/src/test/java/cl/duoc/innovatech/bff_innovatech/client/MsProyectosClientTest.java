package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.TareaRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.TareaResponse;
import cl.duoc.innovatech.bff_innovatech.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias directas de MsProyectosClient (sin Resilience4j ni contexto Spring):
 * verifican que cada método construye la petición HTTP correcta y traduce los
 * errores del microservicio (404) a RecursoNoEncontradoException.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MsProyectosClient - Pruebas Unitarias")
class MsProyectosClientTest {

    @Mock
    private RestTemplate restTemplate;

    private MsProyectosClient client;

    @BeforeEach
    void setUp() {
        client = new MsProyectosClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8081");
    }

    @Test
    @DisplayName("listarProyectos() - retorna la lista del microservicio")
    void listarProyectos_retornaLista() {
        ProyectoResponse p = ProyectoResponse.builder().id(1L).nombre("Portal").build();
        when(restTemplate.exchange(eq("http://localhost:8081/api/v1/proyectos"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(p)));

        List<ProyectoResponse> resultado = client.listarProyectos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Portal");
    }

    @Test
    @DisplayName("listarPorEstado() - filtra por query param")
    void listarPorEstado_retornaFiltrados() {
        ProyectoResponse p = ProyectoResponse.builder().id(1L).estado("PENDIENTE").build();
        when(restTemplate.exchange(eq("http://localhost:8081/api/v1/proyectos?estado=PENDIENTE"), eq(HttpMethod.GET),
                any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(p)));

        List<ProyectoResponse> resultado = client.listarPorEstado("PENDIENTE");

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerProyectoPorId() - retorna proyecto existente")
    void obtenerProyectoPorId_existente() {
        ProyectoResponse p = ProyectoResponse.builder().id(1L).nombre("Portal").build();
        when(restTemplate.getForObject("http://localhost:8081/api/v1/proyectos/1", ProyectoResponse.class))
                .thenReturn(p);

        assertThat(client.obtenerProyectoPorId(1L).getNombre()).isEqualTo("Portal");
    }

    @Test
    @DisplayName("obtenerProyectoPorId() - traduce 404 a RecursoNoEncontradoException")
    void obtenerProyectoPorId_noExistente() {
        when(restTemplate.getForObject(anyString(), eq(ProyectoResponse.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.obtenerProyectoPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crearProyecto() - envía POST y retorna la respuesta")
    void crearProyecto_retornaCreado() {
        ProyectoRequest req = ProyectoRequest.builder().nombre("Portal").build();
        ProyectoResponse resp = ProyectoResponse.builder().id(1L).nombre("Portal").build();
        when(restTemplate.postForEntity(eq("http://localhost:8081/api/v1/proyectos"), any(), eq(ProyectoResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.crearProyecto(req).getNombre()).isEqualTo("Portal");
    }

    @Test
    @DisplayName("actualizarProyecto() - envía PUT y retorna la respuesta")
    void actualizarProyecto_retornaActualizado() {
        ProyectoRequest req = ProyectoRequest.builder().nombre("Portal v2").build();
        ProyectoResponse resp = ProyectoResponse.builder().id(1L).nombre("Portal v2").build();
        when(restTemplate.exchange(eq("http://localhost:8081/api/v1/proyectos/1"), eq(HttpMethod.PUT), any(),
                eq(ProyectoResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.actualizarProyecto(1L, req).getNombre()).isEqualTo("Portal v2");
    }

    @Test
    @DisplayName("actualizarProyecto() - traduce 404 a RecursoNoEncontradoException")
    void actualizarProyecto_noExistente() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(ProyectoResponse.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.actualizarProyecto(99L, ProyectoRequest.builder().build()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("eliminarProyecto() - elimina sin lanzar excepción")
    void eliminarProyecto_eliminaCorrectamente() {
        client.eliminarProyecto(1L);
        // No lanza excepción: delete() es void, basta con que no falle
    }

    @Test
    @DisplayName("eliminarProyecto() - traduce 404 a RecursoNoEncontradoException")
    void eliminarProyecto_noExistente() {
        org.mockito.Mockito.doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null))
                .when(restTemplate).delete(anyString());

        assertThatThrownBy(() -> client.eliminarProyecto(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("listarTareasPorProyecto() - retorna las tareas del proyecto")
    void listarTareasPorProyecto_retornaLista() {
        TareaResponse t = TareaResponse.builder().id(1L).titulo("Diseñar BD").proyectoId(1L).build();
        when(restTemplate.exchange(eq("http://localhost:8081/api/v1/tareas/proyecto/1"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(t)));

        assertThat(client.listarTareasPorProyecto(1L)).hasSize(1);
    }

    @Test
    @DisplayName("obtenerTareaPorId() - retorna tarea existente")
    void obtenerTareaPorId_existente() {
        TareaResponse t = TareaResponse.builder().id(1L).titulo("Diseñar BD").build();
        when(restTemplate.getForObject("http://localhost:8081/api/v1/tareas/1", TareaResponse.class)).thenReturn(t);

        assertThat(client.obtenerTareaPorId(1L).getTitulo()).isEqualTo("Diseñar BD");
    }

    @Test
    @DisplayName("obtenerTareaPorId() - traduce 404 a RecursoNoEncontradoException")
    void obtenerTareaPorId_noExistente() {
        when(restTemplate.getForObject(anyString(), eq(TareaResponse.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.obtenerTareaPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crearTarea() - envía POST y retorna la respuesta")
    void crearTarea_retornaCreada() {
        TareaRequest req = TareaRequest.builder().titulo("Diseñar BD").proyectoId(1L).build();
        TareaResponse resp = TareaResponse.builder().id(1L).titulo("Diseñar BD").build();
        when(restTemplate.postForEntity(eq("http://localhost:8081/api/v1/tareas"), any(), eq(TareaResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.crearTarea(req).getTitulo()).isEqualTo("Diseñar BD");
    }

    @Test
    @DisplayName("actualizarTarea() - envía PUT y retorna la respuesta")
    void actualizarTarea_retornaActualizada() {
        TareaRequest req = TareaRequest.builder().titulo("Implementar API").proyectoId(1L).build();
        TareaResponse resp = TareaResponse.builder().id(1L).titulo("Implementar API").build();
        when(restTemplate.exchange(eq("http://localhost:8081/api/v1/tareas/1"), eq(HttpMethod.PUT), any(),
                eq(TareaResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.actualizarTarea(1L, req).getTitulo()).isEqualTo("Implementar API");
    }

    @Test
    @DisplayName("actualizarTarea() - traduce 404 a RecursoNoEncontradoException")
    void actualizarTarea_noExistente() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(TareaResponse.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.actualizarTarea(99L, TareaRequest.builder().build()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("eliminarTarea() - elimina sin lanzar excepción")
    void eliminarTarea_eliminaCorrectamente() {
        client.eliminarTarea(1L);
    }

    @Test
    @DisplayName("eliminarTarea() - traduce 404 a RecursoNoEncontradoException")
    void eliminarTarea_noExistente() {
        org.mockito.Mockito.doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null))
                .when(restTemplate).delete(anyString());

        assertThatThrownBy(() -> client.eliminarTarea(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
