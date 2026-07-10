package cl.duoc.innovatech.bff_innovatech.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumenRecursosResponse {
    private Long totalEmpleados;
    private Long empleadosDisponibles;
    private Long empleadosOcupados;

    // ms-recursos expone este campo como "totalAsignacionesActivas"
    @JsonAlias("totalAsignacionesActivas")
    private Long asignacionesActivas;
}
