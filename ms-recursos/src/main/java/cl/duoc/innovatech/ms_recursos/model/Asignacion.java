package cl.duoc.innovatech.ms_recursos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @NotNull(message = "El id del proyecto es obligatorio")
    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @NotBlank(message = "El nombre del proyecto no puede estar vacío")
    @Column(name = "nombre_proyecto", nullable = false)
    private String nombreProyecto;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "horas_asignadas")
    @Min(value = 1, message = "Las horas asignadas deben ser al menos 1")
    private Integer horasAsignadas;

    @Column(name = "rol_en_proyecto")
    private String rolEnProyecto;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (activo == null) activo = true;
        if (fechaInicio == null) fechaInicio = LocalDate.now();
    }
}
