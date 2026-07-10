package cl.duoc.innovatech.bff_innovatech.service.impl;

import cl.duoc.innovatech.bff_innovatech.client.MsMensajeriaClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.MensajeRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ConversacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.MensajeResponse;
import cl.duoc.innovatech.bff_innovatech.service.MensajeBffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio BFF para mensajería (chat interno).
 * Delega al cliente HTTP de ms-mensajeria.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeBffServiceImpl implements MensajeBffService {

    private final MsMensajeriaClient msMensajeriaClient;

    @Override
    public MensajeResponse enviar(MensajeRequest request) {
        log.info("BFF → ms-mensajeria: enviar mensaje de {} a {}", request.getRemitenteEmail(), request.getDestinatarioEmail());
        return msMensajeriaClient.enviar(request);
    }

    @Override
    public List<MensajeResponse> obtenerConversacion(String email1, String email2) {
        log.info("BFF → ms-mensajeria: conversacion entre {} y {}", email1, email2);
        return msMensajeriaClient.obtenerConversacion(email1, email2);
    }

    @Override
    public List<ConversacionResponse> obtenerInbox(String email) {
        log.info("BFF → ms-mensajeria: inbox de {}", email);
        return msMensajeriaClient.obtenerInbox(email);
    }

    @Override
    public int marcarComoLeidos(String destinatarioEmail, String remitenteEmail) {
        log.info("BFF → ms-mensajeria: marcar leidos destinatario={} remitente={}", destinatarioEmail, remitenteEmail);
        return msMensajeriaClient.marcarComoLeidos(destinatarioEmail, remitenteEmail);
    }
}
