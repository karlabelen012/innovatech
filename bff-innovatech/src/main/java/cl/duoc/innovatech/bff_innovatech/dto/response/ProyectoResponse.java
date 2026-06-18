package cl.duoc.innovatech.bff_innovatech.dto.response;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProyectoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String estado;
    private Integer avance;
    private String responsable;
}
