package cl.duoc.innovatech.ms_recursos.controller;

import cl.duoc.innovatech.ms_recursos.dto.AsignacionDTO;
import cl.duoc.innovatech.ms_recursos.dto.DisponibilidadDTO;
import cl.duoc.innovatech.ms_recursos.dto.ResumenRecursosDTO;
import cl.duoc.innovatech.ms_recursos.service.AsignacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asignaciones")
@RequiredArgsConstructor
@Tag(name = "Asignaciones", description = "Gestión de asignaciones de empleados a proyectos")
@CrossOrigin(origins = "*")
public class AsignacionController {

    private final AsignacionService asignacionService;

    @PostMapping
    @Operation(summary = "Crear asignación")
    public ResponseEntity<AsignacionDTO> crear(@Valid @RequestBody AsignacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asignacionService.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar asignaciones",
            description = "Sin parámetros retorna todas. Acepta filtros: ?empleadoId=1 | ?proyectoId=5")
    public ResponseEntity<List<AsignacionDTO>> listar(
            @Parameter(description = "Filtrar por ID de empleado")
            @RequestParam(required = false) Long empleadoId,
            @Parameter(description = "Filtrar por ID de proyecto")
            @RequestParam(required = false) Long proyectoId) {

        if (empleadoId != null) return ResponseEntity.ok(asignacionService.obtenerPorEmpleado(empleadoId));
        if (proyectoId != null) return ResponseEntity.ok(asignacionService.obtenerPorProyecto(proyectoId));
        return ResponseEntity.ok(asignacionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener asignación por ID")
    public ResponseEntity<AsignacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asignación")
    public ResponseEntity<AsignacionDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionDTO dto) {
        return ResponseEntity.ok(asignacionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar asignación")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        asignacionService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/equipo")
    @Operation(summary = "Disponibilidad del equipo",
            description = "Retorna horas asignadas y libres por cada empleado activo")
    public ResponseEntity<List<DisponibilidadDTO>> equipo() {
        return ResponseEntity.ok(asignacionService.obtenerDisponibilidadEquipo());
    }

    @GetMapping("/resumen")
    @Operation(summary = "Resumen de recursos",
            description = "Totales: empleados, disponibles, ocupados, asignaciones activas")
    public ResponseEntity<ResumenRecursosDTO> resumen() {
        return ResponseEntity.ok(asignacionService.obtenerResumen());
    }
}
