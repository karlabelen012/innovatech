package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.request.MensajeRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ConversacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.MensajeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MsMensajeriaClient - Pruebas Unitarias")
class MsMensajeriaClientTest {

    @Mock
    private RestTemplate restTemplate;

    private MsMensajeriaClient client;

    @BeforeEach
    void setUp() {
        client = new MsMensajeriaClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8085");
    }

    @Test
    @DisplayName("enviar() - envía POST y retorna la respuesta")
    void enviar_retornaMensajeEnviado() {
        MensajeRequest req = MensajeRequest.builder()
                .remitenteEmail("bryan@innovatech.cl").destinatarioEmail("karla@innovatech.cl")
                .contenido("Hola Karla").build();
        MensajeResponse resp = MensajeResponse.builder().id(1L).contenido("Hola Karla").build();
        when(restTemplate.postForEntity(eq("http://localhost:8085/api/v1/mensajes"), any(), eq(MensajeResponse.class)))
                .thenReturn(ResponseEntity.ok(resp));

        assertThat(client.enviar(req).getContenido()).isEqualTo("Hola Karla");
    }

    @Test
    @DisplayName("obtenerConversacion() - retorna el historial entre dos usuarios")
    void obtenerConversacion_retornaHistorial() {
        MensajeResponse m = MensajeResponse.builder().id(1L).contenido("Hola Karla").build();
        when(restTemplate.exchange(
                eq("http://localhost:8085/api/v1/mensajes/conversacion?email1=bryan@innovatech.cl&email2=karla@innovatech.cl"),
                eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(m)));

        assertThat(client.obtenerConversacion("bryan@innovatech.cl", "karla@innovatech.cl")).hasSize(1);
    }

    @Test
    @DisplayName("obtenerInbox() - retorna el resumen de conversaciones de un usuario")
    void obtenerInbox_retornaResumen() {
        ConversacionResponse conv = ConversacionResponse.builder().contactoEmail("karla@innovatech.cl").build();
        when(restTemplate.exchange(
                eq("http://localhost:8085/api/v1/mensajes/inbox?email=bryan@innovatech.cl"),
                eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(conv)));

        assertThat(client.obtenerInbox("bryan@innovatech.cl")).hasSize(1);
    }

    @Test
    @DisplayName("marcarComoLeidos() - envía PATCH y retorna el conteo actualizado")
    void marcarComoLeidos_retornaConteo() {
        when(restTemplate.exchange(
                eq("http://localhost:8085/api/v1/mensajes/marcar-leidos?destinatario=bryan@innovatech.cl&remitente=karla@innovatech.cl"),
                eq(HttpMethod.PATCH), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(Map.of("actualizados", 2)));

        assertThat(client.marcarComoLeidos("bryan@innovatech.cl", "karla@innovatech.cl")).isEqualTo(2);
    }
}
