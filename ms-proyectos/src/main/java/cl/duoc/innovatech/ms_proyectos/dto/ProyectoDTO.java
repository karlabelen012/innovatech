package cl.duoc.innovatech.ms_proyectos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @Pattern(regexp = "PENDIENTE|EN_PROGRESO|FINALIZADO",
             message = "Estado debe ser PENDIENTE, EN_PROGRESO o FINALIZADO")
    private String estado;

    private Integer avance;

    private String responsable;
}
