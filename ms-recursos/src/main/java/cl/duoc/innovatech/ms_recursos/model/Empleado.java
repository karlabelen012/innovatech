package cl.duoc.innovatech.ms_recursos.model;

import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Column(nullable = false)
    private String telefono;

    @NotNull(message = "El rol no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolEmpleado rol;

    @NotNull(message = "La disponibilidad no puede ser nula")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisponibilidadEstado disponibilidad;

    @Column(name = "horas_semanales")
    @Min(value = 0, message = "Las horas no pueden ser negativas")
    @Max(value = 45, message = "Las horas no pueden superar 45 por semana")
    private Integer horasSemanales;

    @Column(name = "habilidades")
    private String habilidades;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Asignacion> asignaciones;

    @PrePersist
    protected void onCreate() {
        if (fechaIngreso == null) fechaIngreso = LocalDate.now();
        if (activo == null) activo = true;
        if (disponibilidad == null) disponibilidad = DisponibilidadEstado.DISPONIBLE;
    }
}
