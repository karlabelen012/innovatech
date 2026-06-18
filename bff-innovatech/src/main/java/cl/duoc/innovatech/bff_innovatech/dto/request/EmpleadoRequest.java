package cl.duoc.innovatech.bff_innovatech.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmpleadoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    private String telefono;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    private String disponibilidad;

    private Integer horasSemanales;

    private String habilidades;
}
