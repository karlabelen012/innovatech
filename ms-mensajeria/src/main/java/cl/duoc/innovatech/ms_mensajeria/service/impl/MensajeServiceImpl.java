package cl.duoc.innovatech.ms_mensajeria.service.impl;

import cl.duoc.innovatech.ms_mensajeria.dto.ConversacionDTO;
import cl.duoc.innovatech.ms_mensajeria.dto.MensajeDTO;
import cl.duoc.innovatech.ms_mensajeria.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_mensajeria.model.Mensaje;
import cl.duoc.innovatech.ms_mensajeria.repository.MensajeRepository;
import cl.duoc.innovatech.ms_mensajeria.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;

    @Override
    public MensajeDTO enviar(MensajeDTO dto) {
        Mensaje mensaje = Mensaje.builder()
                .remitenteEmail(dto.getRemitenteEmail())
                .remitenteNombre(dto.getRemitenteNombre())
                .destinatarioEmail(dto.getDestinatarioEmail())
                .destinatarioNombre(dto.getDestinatarioNombre())
                .contenido(dto.getContenido())
                .build();
        return toDTO(mensajeRepository.save(mensaje));
    }

    @Override
    public MensajeDTO obtenerPorId(Long id) {
        return toDTO(mensajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje no encontrado con id: " + id)));
    }

    @Override
    public List<MensajeDTO> obtenerConversacion(String email1, String email2) {
        return mensajeRepository.findConversacion(email1, email2).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversacionDTO> obtenerInbox(String email) {
        List<Mensaje> mensajes = mensajeRepository
                .findByRemitenteEmailOrDestinatarioEmailOrderByFechaEnvioDesc(email, email);

        Map<String, List<Mensaje>> porContacto = new LinkedHashMap<>();
        Map<String, String> nombrePorContacto = new HashMap<>();

        for (Mensaje m : mensajes) {
            boolean enviadoPorMi = m.getRemitenteEmail().equalsIgnoreCase(email);
            String contactoEmail = enviadoPorMi ? m.getDestinatarioEmail() : m.getRemitenteEmail();
            String contactoNombre = enviadoPorMi ? m.getDestinatarioNombre() : m.getRemitenteNombre();
            porContacto.computeIfAbsent(contactoEmail, k -> new ArrayList<>()).add(m);
            nombrePorContacto.putIfAbsent(contactoEmail, contactoNombre);
        }

        List<ConversacionDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, List<Mensaje>> entry : porContacto.entrySet()) {
            String contacto = entry.getKey();
            List<Mensaje> hilo = entry.getValue();
            Mensaje ultimo = hilo.get(0);
            long noLeidos = hilo.stream()
                    .filter(m -> m.getDestinatarioEmail().equalsIgnoreCase(email) && !Boolean.TRUE.equals(m.getLeido()))
                    .count();
            resultado.add(ConversacionDTO.builder()
                    .contactoEmail(contacto)
                    .contactoNombre(nombrePorContacto.get(contacto))
                    .ultimoMensaje(ultimo.getContenido())
                    .fechaUltimoMensaje(ultimo.getFechaEnvio())
                    .noLeidos(noLeidos)
                    .ultimoEnviadoPorMi(ultimo.getRemitenteEmail().equalsIgnoreCase(email))
                    .build());
        }
        return resultado;
    }

    @Override
    @Transactional
    public int marcarComoLeidos(String destinatarioEmail, String remitenteEmail) {
        return mensajeRepository.marcarComoLeidos(destinatarioEmail, remitenteEmail);
    }

    private MensajeDTO toDTO(Mensaje m) {
        return MensajeDTO.builder()
                .id(m.getId())
                .remitenteEmail(m.getRemitenteEmail())
                .remitenteNombre(m.getRemitenteNombre())
                .destinatarioEmail(m.getDestinatarioEmail())
                .destinatarioNombre(m.getDestinatarioNombre())
                .contenido(m.getContenido())
                .fechaEnvio(m.getFechaEnvio())
                .leido(m.getLeido())
                .build();
    }
}
