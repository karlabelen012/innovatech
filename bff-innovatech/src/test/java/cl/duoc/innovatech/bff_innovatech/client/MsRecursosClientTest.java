package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MsRecursosClient - Pruebas Unitarias")
class MsRecursosClientTest {

    @Mock
    private RestTemplate restTemplate;

    private MsRecursosClient client;

    @BeforeEach
    void setUp() {
        client = new MsRecursosClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8084");
    }

    @Test
    @DisplayName("listarEmpleados() - retorna la lista del microservicio")
    void listarEmpleados_retornaLista() {
        EmpleadoResponse e = EmpleadoResponse.builder().id(1L).nombre("Karla").build();
        when(restTemplate.exchange(eq("http://localhost:8084/api/v1/empleados"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(e)));

        assertThat(client.listarEmpleados()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerEmpleadoPorId() - retorna empleado existente")
    void obtenerEmpleadoPorId_existente() {
        EmpleadoResponse e = EmpleadoResponse.builder().id(1L).nombre("Karla").build();
        when(restTemplate.getForObject("http://localhost:8084/api/v1/empleados/1", EmpleadoResponse.class)).thenReturn(e);

        assertThat(client.obtenerEmpleadoPorId(1L).getNombre()).isEqualTo("Karla");
    }

    @Test
    @DisplayName("obtenerEmpleadoPorId() - traduce 404 a RecursoNoEncontradoException")
    void obtenerEmpleadoPorId_noExistente() {
        when(restTemplate.getForObject(anyString(), eq(EmpleadoResponse.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.obtenerEmpleadoPorId(99L)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("crearEmpleado() - envía POST y retorna la respuesta")
    void crearEmpleado_retornaCreado() {
        EmpleadoRequest req = EmpleadoRequest.builder().nombre("Karla").email("karla@innovatech.cl").build();
        EmpleadoResponse resp = EmpleadoResponse.builder().id(1L).nombre("Karla").build();
        when(restTemplate.postForEntity(eq("http://localhost:8084/api/v1/empleados"), any(), eq(EmpleadoResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.crearEmpleado(req).getNombre()).isEqualTo("Karla");
    }

    @Test
    @DisplayName("actualizarEmpleado() - envía PUT y retorna la respuesta")
    void actualizarEmpleado_retornaActualizado() {
        EmpleadoRequest req = EmpleadoRequest.builder().nombre("Karla Herrera").build();
        EmpleadoResponse resp = EmpleadoResponse.builder().id(1L).nombre("Karla Herrera").build();
        when(restTemplate.exchange(eq("http://localhost:8084/api/v1/empleados/1"), eq(HttpMethod.PUT), any(),
                eq(EmpleadoResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.actualizarEmpleado(1L, req).getNombre()).isEqualTo("Karla Herrera");
    }

    @Test
    @DisplayName("actualizarEmpleado() - traduce 404 a RecursoNoEncontradoException")
    void actualizarEmpleado_noExistente() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(EmpleadoResponse.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.actualizarEmpleado(99L, EmpleadoRequest.builder().build()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("actualizarDisponibilidad() - envía PATCH y retorna la respuesta")
    void actualizarDisponibilidad_retornaActualizado() {
        EmpleadoResponse resp = EmpleadoResponse.builder().id(1L).disponibilidad("OCUPADO").build();
        when(restTemplate.exchange(eq("http://localhost:8084/api/v1/empleados/1?disponibilidad=OCUPADO"),
                eq(HttpMethod.PATCH), any(), eq(EmpleadoResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.actualizarDisponibilidad(1L, "OCUPADO").getDisponibilidad()).isEqualTo("OCUPADO");
    }

    @Test
    @DisplayName("eliminarEmpleado() - elimina sin lanzar excepción")
    void eliminarEmpleado_eliminaCorrectamente() {
        client.eliminarEmpleado(1L);
    }

    @Test
    @DisplayName("eliminarEmpleado() - traduce 404 a RecursoNoEncontradoException")
    void eliminarEmpleado_noExistente() {
        doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null))
                .when(restTemplate).delete(anyString());

        assertThatThrownBy(() -> client.eliminarEmpleado(99L)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("listarAsignaciones() - retorna la lista del microservicio")
    void listarAsignaciones_retornaLista() {
        AsignacionResponse a = AsignacionResponse.builder().id(1L).empleadoId(1L).proyectoId(5L).build();
        when(restTemplate.exchange(eq("http://localhost:8084/api/v1/asignaciones"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(a)));

        assertThat(client.listarAsignaciones()).hasSize(1);
    }

    @Test
    @DisplayName("crearAsignacion() - envía POST y retorna la respuesta")
    void crearAsignacion_retornaCreada() {
        AsignacionRequest req = AsignacionRequest.builder().empleadoId(1L).proyectoId(5L).build();
        AsignacionResponse resp = AsignacionResponse.builder().id(1L).empleadoId(1L).proyectoId(5L).build();
        when(restTemplate.postForEntity(eq("http://localhost:8084/api/v1/asignaciones"), any(), eq(AsignacionResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.crearAsignacion(req).getProyectoId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("desactivarAsignacion() - elimina sin lanzar excepción")
    void desactivarAsignacion_eliminaCorrectamente() {
        client.desactivarAsignacion(10L);
    }

    @Test
    @DisplayName("obtenerResumen() - retorna el resumen del microservicio")
    void obtenerResumen_retornaResumen() {
        ResumenRecursosResponse resp = ResumenRecursosResponse.builder().totalEmpleados(10L).build();
        when(restTemplate.getForObject("http://localhost:8084/api/v1/asignaciones/resumen", ResumenRecursosResponse.class))
                .thenReturn(resp);

        assertThat(client.obtenerResumen().getTotalEmpleados()).isEqualTo(10L);
    }
}
