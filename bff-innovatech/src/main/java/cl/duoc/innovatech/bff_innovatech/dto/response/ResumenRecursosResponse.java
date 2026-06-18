package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumenRecursosResponse {
    private Long totalEmpleados;
    private Long empleadosDisponibles;
    private Long empleadosOcupados;
    private Long asignacionesActivas;
}
