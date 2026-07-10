package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConversacionResponse {
    private String contactoEmail;
    private String contactoNombre;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private long noLeidos;
    private boolean ultimoEnviadoPorMi;
}
