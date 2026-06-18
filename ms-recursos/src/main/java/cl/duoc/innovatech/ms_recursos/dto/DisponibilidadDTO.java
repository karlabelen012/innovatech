package cl.duoc.innovatech.ms_recursos.dto;

import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadDTO {
    private Long empleadoId;
    private String nombre;
    private String apellido;
    private RolEmpleado rol;
    private DisponibilidadEstado disponibilidad;
    private Integer horasSemanales;
    private Integer horasAsignadas;
    private Integer horasLibres;
}
