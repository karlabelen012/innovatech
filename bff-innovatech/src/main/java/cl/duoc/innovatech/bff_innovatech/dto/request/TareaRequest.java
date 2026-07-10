package cl.duoc.innovatech.bff_innovatech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TareaRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    private String estado;

    private String responsable;

    @NotNull(message = "El proyecto al que pertenece la tarea es obligatorio")
    private Long proyectoId;
}
