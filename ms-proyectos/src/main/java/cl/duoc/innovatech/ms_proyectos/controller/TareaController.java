package cl.duoc.innovatech.ms_proyectos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.innovatech.ms_proyectos.dto.TareaDTO;
import cl.duoc.innovatech.ms_proyectos.service.TareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tareas")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "Gestión de tareas asociadas a proyectos")
public class TareaController {

    private final TareaService tareaService;

    @GetMapping
    @Operation(summary = "Listar todas las tareas")
    public ResponseEntity<List<TareaDTO>> listar() {
        return ResponseEntity.ok(tareaService.listar());
    }

    @GetMapping("/proyecto/{proyectoId}")
    @Operation(summary = "Listar tareas de un proyecto")
    public ResponseEntity<List<TareaDTO>> listarPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(tareaService.listarPorProyecto(proyectoId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tarea por ID")
    public ResponseEntity<TareaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tareaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear tarea")
    public ResponseEntity<TareaDTO> crear(@Valid @RequestBody TareaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tarea")
    public ResponseEntity<TareaDTO> actualizar(@PathVariable Long id,
                                                @Valid @RequestBody TareaDTO dto) {
        return ResponseEntity.ok(tareaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tarea")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tareaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
