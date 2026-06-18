package cl.duoc.innovatech.ms_analitica.model;

import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_proyecto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proyecto no puede estar vacío")
    @Column(name = "nombre_proyecto", nullable = false)
    private String nombreProyecto;

    @NotNull(message = "El ID externo del proyecto es requerido")
    @Column(name = "proyecto_id_externo", nullable = false)
    private Long proyectoIdExterno;

    @NotNull(message = "El estado no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProyecto estado;

    @Min(value = 0, message = "El avance no puede ser negativo")
    @Max(value = 100, message = "El avance no puede superar el 100%")
    @Column(name = "porcentaje_avance")
    private Integer porcentajeAvance;

    @Column(name = "total_tareas")
    private Integer totalTareas;

    @Column(name = "tareas_completadas")
    private Integer tareasCompletadas;

    @Column(name = "tareas_pendientes")
    private Integer tareasPendientes;

    @Column(name = "recursos_asignados")
    private Integer recursosAsignados;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_estimada")
    private LocalDate fechaFinEstimada;

    @Column(name = "fecha_registro", nullable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
