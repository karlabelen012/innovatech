package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MensajeResponse {
    private Long id;
    private String remitenteEmail;
    private String remitenteNombre;
    private String destinatarioEmail;
    private String destinatarioNombre;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
}
