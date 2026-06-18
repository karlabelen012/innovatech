package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AsignacionResponse {
    private Long id;
    private Long empleadoId;
    private Long proyectoId;
    private String nombreProyecto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer horasAsignadas;
    private String rolEnProyecto;
    private Boolean activo;
}
