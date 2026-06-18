package cl.duoc.innovatech.ms_analitica.dto;

import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiMetricaDTO {

    private Long id;

    @NotBlank(message = "El nombre del KPI no puede estar vacío")
    private String nombreKpi;

    @NotNull(message = "La categoría no puede ser nula")
    private CategoriaKpi categoria;

    @NotNull(message = "El valor no puede ser nulo")
    private Double valor;

    private String unidad;
    private String descripcion;
}
