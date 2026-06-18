package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmpleadoResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;
    private String disponibilidad;
    private Integer horasSemanales;
    private String habilidades;
    private LocalDate fechaIngreso;
    private Boolean activo;
}
