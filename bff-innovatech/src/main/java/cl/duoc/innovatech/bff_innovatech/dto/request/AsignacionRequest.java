package cl.duoc.innovatech.bff_innovatech.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AsignacionRequest {

    @NotNull(message = "El empleadoId es obligatorio")
    private Long empleadoId;

    @NotNull(message = "El proyectoId es obligatorio")
    private Long proyectoId;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    private String nombreProyecto;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @Min(value = 1, message = "Mínimo 1 hora asignada")
    private Integer horasAsignadas;

    private String rolEnProyecto;
}
