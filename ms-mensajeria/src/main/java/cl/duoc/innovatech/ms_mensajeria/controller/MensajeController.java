package cl.duoc.innovatech.ms_mensajeria.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.innovatech.ms_mensajeria.dto.ConversacionDTO;
import cl.duoc.innovatech.ms_mensajeria.dto.MensajeDTO;
import cl.duoc.innovatech.ms_mensajeria.service.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mensajes")
@RequiredArgsConstructor
@Tag(name = "Mensajería", description = "Chat interno entre usuarios de Innovatech Solutions")
public class MensajeController {

    private final MensajeService mensajeService;

    @PostMapping
    @Operation(summary = "Enviar un mensaje")
    public ResponseEntity<MensajeDTO> enviar(@Valid @RequestBody MensajeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajeService.enviar(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un mensaje por ID")
    public ResponseEntity<MensajeDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.obtenerPorId(id));
    }

    @GetMapping("/conversacion")
    @Operation(summary = "Obtener el historial de mensajes entre dos usuarios")
    public ResponseEntity<List<MensajeDTO>> obtenerConversacion(
            @RequestParam String email1, @RequestParam String email2) {
        return ResponseEntity.ok(mensajeService.obtenerConversacion(email1, email2));
    }

    @GetMapping("/inbox")
    @Operation(summary = "Obtener la bandeja de entrada (resumen de conversaciones) de un usuario")
    public ResponseEntity<List<ConversacionDTO>> obtenerInbox(@RequestParam String email) {
        return ResponseEntity.ok(mensajeService.obtenerInbox(email));
    }

    @PatchMapping("/marcar-leidos")
    @Operation(summary = "Marcar como leídos todos los mensajes de un remitente hacia un destinatario")
    public ResponseEntity<Map<String, Integer>> marcarComoLeidos(
            @RequestParam String destinatario, @RequestParam String remitente) {
        int actualizados = mensajeService.marcarComoLeidos(destinatario, remitente);
        return ResponseEntity.ok(Map.of("actualizados", actualizados));
    }
}
