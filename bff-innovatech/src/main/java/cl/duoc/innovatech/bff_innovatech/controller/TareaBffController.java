package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.TareaRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.TareaResponse;
import cl.duoc.innovatech.bff_innovatech.service.TareaBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador BFF para gestión de tareas.
 * Expone /api/bff/tareas y delega al ms-proyectos a través del service.
 */
@RestController
@RequestMapping("/api/bff/tareas")
@RequiredArgsConstructor
@Tag(name = "BFF – Tareas", description = "Proxy del BFF hacia las tareas de ms-proyectos")
@CrossOrigin(origins = "*")
public class TareaBffController {

    private final TareaBffService tareaBffService;

    @GetMapping("/proyecto/{proyectoId}")
    @Operation(summary = "Listar tareas de un proyecto")
    public ResponseEntity<List<TareaResponse>> listarPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(tareaBffService.listarPorProyecto(proyectoId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tarea por ID")
    public ResponseEntity<TareaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tareaBffService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear tarea")
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaBffService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tarea")
    public ResponseEntity<TareaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequest request) {
        return ResponseEntity.ok(tareaBffService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tarea")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tareaBffService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
