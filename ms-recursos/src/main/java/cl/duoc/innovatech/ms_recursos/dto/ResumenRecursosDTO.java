package cl.duoc.innovatech.ms_recursos.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenRecursosDTO {
    private Long totalEmpleados;
    private Long empleadosDisponibles;
    private Long empleadosOcupados;
    private Long empleadosEnVacaciones;
    private Long totalAsignacionesActivas;
}
