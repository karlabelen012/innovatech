package cl.duoc.innovatech.ms_recursos.dto;

import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private RolEmpleado rol;

    private DisponibilidadEstado disponibilidad;

    private Integer horasSemanales;

    private String habilidades;

    private LocalDate fechaIngreso;

    private Boolean activo;
}
