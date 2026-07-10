package cl.duoc.innovatech.ms_mensajeria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversacionDTO {
    private String contactoEmail;
    private String contactoNombre;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private long noLeidos;
    private boolean ultimoEnviadoPorMi;
}
