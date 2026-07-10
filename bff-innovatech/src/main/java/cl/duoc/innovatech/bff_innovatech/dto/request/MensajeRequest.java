package cl.duoc.innovatech.bff_innovatech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MensajeRequest {

    @NotBlank(message = "El email del remitente es obligatorio")
    private String remitenteEmail;

    private String remitenteNombre;

    @NotBlank(message = "El email del destinatario es obligatorio")
    private String destinatarioEmail;

    private String destinatarioNombre;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
    private String contenido;
}
