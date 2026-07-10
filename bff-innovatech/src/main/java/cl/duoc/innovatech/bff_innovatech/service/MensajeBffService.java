package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.dto.request.MensajeRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ConversacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.MensajeResponse;

import java.util.List;

public interface MensajeBffService {
    MensajeResponse enviar(MensajeRequest request);
    List<MensajeResponse> obtenerConversacion(String email1, String email2);
    List<ConversacionResponse> obtenerInbox(String email);
    int marcarComoLeidos(String destinatarioEmail, String remitenteEmail);
}
