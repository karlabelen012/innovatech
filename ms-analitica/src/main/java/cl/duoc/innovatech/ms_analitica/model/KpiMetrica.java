package cl.duoc.innovatech.ms_analitica.model;

import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kpi_metricas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiMetrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del KPI no puede estar vacío")
    @Column(name = "nombre_kpi", nullable = false)
    private String nombreKpi;

    @NotNull(message = "La categoría no puede ser nula")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaKpi categoria;

    @NotNull(message = "El valor no puede ser nulo")
    @Column(nullable = false)
    private Double valor;

    @Column
    private String unidad;

    @Column
    private String descripcion;

    @Column(name = "fecha_calculo", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCalculo = LocalDateTime.now();
}
