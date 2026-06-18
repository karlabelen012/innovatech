package cl.duoc.innovatech.bff_innovatech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProyectoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @Pattern(regexp = "PENDIENTE|EN_PROGRESO|FINALIZADO",
             message = "Estado debe ser PENDIENTE, EN_PROGRESO o FINALIZADO")
    private String estado;

    private Integer avance;

    private String responsable;
}
