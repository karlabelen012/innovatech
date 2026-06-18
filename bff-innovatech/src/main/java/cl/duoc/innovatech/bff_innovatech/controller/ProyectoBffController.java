package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.ProyectoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ProyectoResponse;
import cl.duoc.innovatech.bff_innovatech.service.ProyectoBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador BFF para gestión de proyectos.
 * Expone /api/bff/proyectos y delega al ms-proyectos a través del service.
 */
@RestController
@RequestMapping("/api/bff/proyectos")
@RequiredArgsConstructor
@Tag(name = "BFF – Proyectos", description = "Proxy del BFF hacia ms-proyectos")
@CrossOrigin(origins = "*")
public class ProyectoBffController {

    private final ProyectoBffService proyectoBffService;

    @GetMapping
    @Operation(summary = "Listar proyectos",
               description = "Sin filtros devuelve todos; con ?estado= filtra por PENDIENTE, EN_PROGRESO, FINALIZADO")
    public ResponseEntity<List<ProyectoResponse>> listar(
            @Parameter(description = "PENDIENTE | EN_PROGRESO | FINALIZADO")
            @RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            return ResponseEntity.ok(proyectoBffService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(proyectoBffService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proyecto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proyecto encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "503", description = "ms-proyectos no disponible")
    })
    public ResponseEntity<ProyectoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoBffService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear proyecto")
    @ApiResponse(responseCode = "201", description = "Proyecto creado")
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoBffService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proyecto")
    public ResponseEntity<ProyectoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProyectoRequest request) {
        return ResponseEntity.ok(proyectoBffService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proyecto")
    @ApiResponse(responseCode = "204", description = "Proyecto eliminado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoBffService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
