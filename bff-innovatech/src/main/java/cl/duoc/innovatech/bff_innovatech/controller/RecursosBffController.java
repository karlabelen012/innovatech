package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.service.RecursosBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador BFF para gestión de recursos humanos y asignaciones.
 * Proxy hacia ms-recursos (8082).
 */
@RestController
@RequestMapping("/api/bff")
@RequiredArgsConstructor
@Tag(name = "BFF – Recursos", description = "Proxy del BFF hacia ms-recursos")
@CrossOrigin(origins = "*")
public class RecursosBffController {

    private final RecursosBffService recursosBffService;

    // ── EMPLEADOS ──────────────────────────────────────────────────────────────

    @GetMapping("/empleados")
    @Operation(summary = "Listar empleados")
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleados() {
        return ResponseEntity.ok(recursosBffService.listarEmpleados());
    }

    @GetMapping("/empleados/{id}")
    @Operation(summary = "Obtener empleado por ID")
    public ResponseEntity<EmpleadoResponse> obtenerEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(recursosBffService.obtenerEmpleadoPorId(id));
    }

    @PostMapping("/empleados")
    @Operation(summary = "Crear empleado")
    public ResponseEntity<EmpleadoResponse> crearEmpleado(@Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recursosBffService.crearEmpleado(request));
    }

    @PutMapping("/empleados/{id}")
    @Operation(summary = "Actualizar empleado")
    public ResponseEntity<EmpleadoResponse> actualizarEmpleado(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.ok(recursosBffService.actualizarEmpleado(id, request));
    }

    @PatchMapping("/empleados/{id}/disponibilidad")
    @Operation(summary = "Cambiar disponibilidad del empleado",
               description = "Parámetro: ?disponibilidad=DISPONIBLE | OCUPADO | VACACIONES | LICENCIA")
    public ResponseEntity<EmpleadoResponse> actualizarDisponibilidad(
            @PathVariable Long id,
            @RequestParam String disponibilidad) {
        return ResponseEntity.ok(recursosBffService.actualizarDisponibilidad(id, disponibilidad));
    }

    @DeleteMapping("/empleados/{id}")
    @Operation(summary = "Eliminar empleado (baja lógica)")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        recursosBffService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    // ── ASIGNACIONES ───────────────────────────────────────────────────────────

    @GetMapping("/asignaciones")
    @Operation(summary = "Listar asignaciones")
    public ResponseEntity<List<AsignacionResponse>> listarAsignaciones() {
        return ResponseEntity.ok(recursosBffService.listarAsignaciones());
    }

    @PostMapping("/asignaciones")
    @Operation(summary = "Crear asignación empleado-proyecto")
    public ResponseEntity<AsignacionResponse> crearAsignacion(@Valid @RequestBody AsignacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recursosBffService.crearAsignacion(request));
    }

    @DeleteMapping("/asignaciones/{id}")
    @Operation(summary = "Desactivar asignación")
    public ResponseEntity<Void> desactivarAsignacion(@PathVariable Long id) {
        recursosBffService.desactivarAsignacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recursos/resumen")
    @Operation(summary = "Resumen de recursos humanos",
               description = "Totales de empleados, disponibles, ocupados y asignaciones activas")
    public ResponseEntity<ResumenRecursosResponse> resumen() {
        return ResponseEntity.ok(recursosBffService.obtenerResumen());
    }
}
