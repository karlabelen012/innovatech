package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TareaResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private String responsable;
    private Long proyectoId;
}
