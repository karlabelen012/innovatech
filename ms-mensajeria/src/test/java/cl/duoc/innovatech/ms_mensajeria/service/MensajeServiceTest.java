package cl.duoc.innovatech.ms_mensajeria.service;

import cl.duoc.innovatech.ms_mensajeria.dto.ConversacionDTO;
import cl.duoc.innovatech.ms_mensajeria.dto.MensajeDTO;
import cl.duoc.innovatech.ms_mensajeria.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_mensajeria.model.Mensaje;
import cl.duoc.innovatech.ms_mensajeria.repository.MensajeRepository;
import cl.duoc.innovatech.ms_mensajeria.service.impl.MensajeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @InjectMocks
    private MensajeServiceImpl mensajeService;

    private Mensaje mensaje;
    private MensajeDTO mensajeDTO;

    @BeforeEach
    void setUp() {
        mensaje = Mensaje.builder()
                .id(1L)
                .remitenteEmail("bryan@innovatech.cl")
                .remitenteNombre("Bryan Muñoz")
                .destinatarioEmail("karla@innovatech.cl")
                .destinatarioNombre("Karla Herrera")
                .contenido("Hola, ¿cómo va el proyecto?")
                .fechaEnvio(LocalDateTime.of(2026, 6, 26, 10, 0))
                .leido(false)
                .build();

        mensajeDTO = MensajeDTO.builder()
                .remitenteEmail("bryan@innovatech.cl")
                .remitenteNombre("Bryan Muñoz")
                .destinatarioEmail("karla@innovatech.cl")
                .destinatarioNombre("Karla Herrera")
                .contenido("Hola, ¿cómo va el proyecto?")
                .build();
    }

    @Test
    void enviar_retornaMensajeGuardado() {
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);
        MensajeDTO resultado = mensajeService.enviar(mensajeDTO);
        assertThat(resultado.getContenido()).isEqualTo("Hola, ¿cómo va el proyecto?");
        assertThat(resultado.getRemitenteEmail()).isEqualTo("bryan@innovatech.cl");
    }

    @Test
    void obtenerPorId_existente_retornaMensaje() {
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensaje));
        MensajeDTO resultado = mensajeService.obtenerPorId(1L);
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(mensajeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mensajeService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtenerConversacion_retornaMensajesOrdenados() {
        when(mensajeRepository.findConversacion("bryan@innovatech.cl", "karla@innovatech.cl"))
                .thenReturn(List.of(mensaje));
        List<MensajeDTO> resultado = mensajeService.obtenerConversacion("bryan@innovatech.cl", "karla@innovatech.cl");
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getContenido()).isEqualTo("Hola, ¿cómo va el proyecto?");
    }

    @Test
    void obtenerInbox_agrupaPorContactoYCalculaNoLeidos() {
        Mensaje recibido1 = Mensaje.builder()
                .id(1L).remitenteEmail("karla@innovatech.cl").remitenteNombre("Karla Herrera")
                .destinatarioEmail("bryan@innovatech.cl").destinatarioNombre("Bryan Muñoz")
                .contenido("Vamos al 80%").fechaEnvio(LocalDateTime.of(2026, 6, 26, 11, 0)).leido(false)
                .build();
        Mensaje recibido2 = Mensaje.builder()
                .id(2L).remitenteEmail("karla@innovatech.cl").remitenteNombre("Karla Herrera")
                .destinatarioEmail("bryan@innovatech.cl").destinatarioNombre("Bryan Muñoz")
                .contenido("Casi terminamos").fechaEnvio(LocalDateTime.of(2026, 6, 26, 10, 0)).leido(false)
                .build();

        when(mensajeRepository.findByRemitenteEmailOrDestinatarioEmailOrderByFechaEnvioDesc(
                "bryan@innovatech.cl", "bryan@innovatech.cl"))
                .thenReturn(List.of(recibido1, recibido2));

        List<ConversacionDTO> inbox = mensajeService.obtenerInbox("bryan@innovatech.cl");

        assertThat(inbox).hasSize(1);
        ConversacionDTO conv = inbox.get(0);
        assertThat(conv.getContactoEmail()).isEqualTo("karla@innovatech.cl");
        assertThat(conv.getUltimoMensaje()).isEqualTo("Vamos al 80%");
        assertThat(conv.getNoLeidos()).isEqualTo(2);
        assertThat(conv.isUltimoEnviadoPorMi()).isFalse();
    }

    @Test
    void marcarComoLeidos_invocaRepositorio() {
        when(mensajeRepository.marcarComoLeidos("bryan@innovatech.cl", "karla@innovatech.cl")).thenReturn(3);
        int actualizados = mensajeService.marcarComoLeidos("bryan@innovatech.cl", "karla@innovatech.cl");
        assertThat(actualizados).isEqualTo(3);
    }
}
