package cl.duoc.innovatech.ms_proyectos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private String estado = "PENDIENTE"; // PENDIENTE, EN_PROGRESO, FINALIZADO

    @Builder.Default
    @Column(nullable = false)
    private Integer avance = 0; // porcentaje 0-100

    private String responsable;
}

