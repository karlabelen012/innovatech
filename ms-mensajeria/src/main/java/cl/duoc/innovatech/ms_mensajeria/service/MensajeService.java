package cl.duoc.innovatech.ms_mensajeria.service;

import cl.duoc.innovatech.ms_mensajeria.dto.ConversacionDTO;
import cl.duoc.innovatech.ms_mensajeria.dto.MensajeDTO;

import java.util.List;

public interface MensajeService {
    MensajeDTO enviar(MensajeDTO dto);
    MensajeDTO obtenerPorId(Long id);
    List<MensajeDTO> obtenerConversacion(String email1, String email2);
    List<ConversacionDTO> obtenerInbox(String email);
    int marcarComoLeidos(String destinatarioEmail, String remitenteEmail);
}
