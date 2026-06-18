package cl.duoc.innovatech.ms_recursos.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionDTO {

    private Long id;

    @NotNull(message = "El id del empleado es obligatorio")
    private Long empleadoId;

    private String empleadoNombre;

    @NotNull(message = "El id del proyecto es obligatorio")
    private Long proyectoId;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    private String nombreProyecto;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Integer horasAsignadas;

    private String rolEnProyecto;

    private Boolean activo;
}
