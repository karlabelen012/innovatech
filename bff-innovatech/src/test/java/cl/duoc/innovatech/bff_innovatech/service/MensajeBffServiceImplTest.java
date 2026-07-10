package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.client.MsMensajeriaClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.MensajeRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ConversacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.MensajeResponse;
import cl.duoc.innovatech.bff_innovatech.service.impl.MensajeBffServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MensajeBffServiceImpl - Pruebas Unitarias")
class MensajeBffServiceImplTest {

    @Mock
    private MsMensajeriaClient msMensajeriaClient;

    @InjectMocks
    private MensajeBffServiceImpl service;

    private MensajeResponse mensajeMock;
    private MensajeRequest mensajeRequest;

    @BeforeEach
    void setUp() {
        mensajeMock = MensajeResponse.builder()
                .id(1L).remitenteEmail("bryan@innovatech.cl").destinatarioEmail("karla@innovatech.cl")
                .contenido("Hola Karla").leido(false).build();

        mensajeRequest = MensajeRequest.builder()
                .remitenteEmail("bryan@innovatech.cl").destinatarioEmail("karla@innovatech.cl")
                .contenido("Hola Karla").build();
    }

    @Test
    @DisplayName("enviar() - retorna mensaje enviado")
    void enviar_debeRetornarMensajeEnviado() {
        when(msMensajeriaClient.enviar(mensajeRequest)).thenReturn(mensajeMock);

        MensajeResponse resultado = service.enviar(mensajeRequest);

        assertThat(resultado.getContenido()).isEqualTo("Hola Karla");
        verify(msMensajeriaClient).enviar(mensajeRequest);
    }

    @Test
    @DisplayName("obtenerConversacion() - retorna historial de mensajes")
    void obtenerConversacion_debeRetornarHistorial() {
        when(msMensajeriaClient.obtenerConversacion("bryan@innovatech.cl", "karla@innovatech.cl"))
                .thenReturn(List.of(mensajeMock));

        List<MensajeResponse> resultado = service.obtenerConversacion("bryan@innovatech.cl", "karla@innovatech.cl");

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerInbox() - retorna resumen de conversaciones")
    void obtenerInbox_debeRetornarResumen() {
        ConversacionResponse conv = ConversacionResponse.builder()
                .contactoEmail("karla@innovatech.cl").ultimoMensaje("Hola Karla").noLeidos(1).build();
        when(msMensajeriaClient.obtenerInbox("bryan@innovatech.cl")).thenReturn(List.of(conv));

        List<ConversacionResponse> resultado = service.obtenerInbox("bryan@innovatech.cl");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getContactoEmail()).isEqualTo("karla@innovatech.cl");
    }

    @Test
    @DisplayName("marcarComoLeidos() - delega correctamente al cliente")
    void marcarComoLeidos_debeDelegarAlCliente() {
        when(msMensajeriaClient.marcarComoLeidos("bryan@innovatech.cl", "karla@innovatech.cl")).thenReturn(2);

        int resultado = service.marcarComoLeidos("bryan@innovatech.cl", "karla@innovatech.cl");

        assertThat(resultado).isEqualTo(2);
    }
}
