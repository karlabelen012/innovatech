package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.MensajeRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ConversacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.MensajeResponse;
import cl.duoc.innovatech.bff_innovatech.service.MensajeBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador BFF para mensajería interna (chat).
 * Expone /api/bff/mensajes y delega a ms-mensajeria a través del service.
 */
@RestController
@RequestMapping("/api/bff/mensajes")
@RequiredArgsConstructor
@Tag(name = "BFF – Mensajería", description = "Proxy del BFF hacia ms-mensajeria (chat interno)")
@CrossOrigin(origins = "*")
public class MensajeBffController {

    private final MensajeBffService mensajeBffService;

    @PostMapping
    @Operation(summary = "Enviar un mensaje")
    public ResponseEntity<MensajeResponse> enviar(@Valid @RequestBody MensajeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajeBffService.enviar(request));
    }

    @GetMapping("/conversacion")
    @Operation(summary = "Obtener el historial de mensajes entre dos usuarios")
    public ResponseEntity<List<MensajeResponse>> obtenerConversacion(
            @RequestParam String email1, @RequestParam String email2) {
        return ResponseEntity.ok(mensajeBffService.obtenerConversacion(email1, email2));
    }

    @GetMapping("/inbox")
    @Operation(summary = "Obtener la bandeja de entrada (resumen de conversaciones) de un usuario")
    public ResponseEntity<List<ConversacionResponse>> obtenerInbox(@RequestParam String email) {
        return ResponseEntity.ok(mensajeBffService.obtenerInbox(email));
    }

    @PatchMapping("/marcar-leidos")
    @Operation(summary = "Marcar como leídos los mensajes de un remitente hacia un destinatario")
    public ResponseEntity<Map<String, Integer>> marcarComoLeidos(
            @RequestParam String destinatario, @RequestParam String remitente) {
        int actualizados = mensajeBffService.marcarComoLeidos(destinatario, remitente);
        return ResponseEntity.ok(Map.of("actualizados", actualizados));
    }
}
