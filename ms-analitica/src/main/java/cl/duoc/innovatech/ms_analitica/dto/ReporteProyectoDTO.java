package cl.duoc.innovatech.ms_analitica.dto;

import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteProyectoDTO {

    private Long id;

    @NotBlank(message = "El nombre del proyecto no puede estar vacío")
    private String nombreProyecto;

    @NotNull(message = "El ID externo del proyecto es requerido")
    private Long proyectoIdExterno;

    @NotNull(message = "El estado no puede ser nulo")
    private EstadoProyecto estado;

    @Min(0) @Max(100)
    private Integer porcentajeAvance;

    private Integer totalTareas;
    private Integer tareasCompletadas;
    private Integer tareasPendientes;
    private Integer recursosAsignados;
    private LocalDate fechaInicio;
    private LocalDate fechaFinEstimada;
}
