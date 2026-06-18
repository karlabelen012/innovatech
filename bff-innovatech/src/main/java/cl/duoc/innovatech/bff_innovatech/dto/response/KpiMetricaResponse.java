package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KpiMetricaResponse {
    private Long id;
    private String nombreKpi;
    private String categoria;
    private Double valor;
    private String unidad;
    private String descripcion;
    private LocalDateTime fechaCalculo;
}
