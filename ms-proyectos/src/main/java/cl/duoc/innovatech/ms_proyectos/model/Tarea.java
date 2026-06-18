package cl.duoc.innovatech.ms_proyectos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private String estado = "PENDIENTE"; // PENDIENTE, EN_PROGRESO, FINALIZADO

    private String responsable;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;
}
