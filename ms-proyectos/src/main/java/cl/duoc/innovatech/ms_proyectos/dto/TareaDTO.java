package cl.duoc.innovatech.ms_proyectos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaDTO {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @Pattern(regexp = "PENDIENTE|EN_PROGRESO|FINALIZADO",
             message = "Estado debe ser PENDIENTE, EN_PROGRESO o FINALIZADO")
    private String estado;

    private String responsable;

    @NotNull(message = "El proyecto al que pertenece la tarea es obligatorio")
    private Long proyectoId;
}
