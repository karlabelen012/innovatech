package cl.duoc.innovatech.ms_mensajeria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeDTO {

    private Long id;

    @NotBlank(message = "El email del remitente es obligatorio")
    private String remitenteEmail;

    private String remitenteNombre;

    @NotBlank(message = "El email del destinatario es obligatorio")
    private String destinatarioEmail;

    private String destinatarioNombre;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
    private String contenido;

    private LocalDateTime fechaEnvio;

    private Boolean leido;
}
