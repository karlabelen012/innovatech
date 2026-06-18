package cl.duoc.innovatech.ms_recursos.controller;

import cl.duoc.innovatech.ms_recursos.dto.EmpleadoDTO;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import cl.duoc.innovatech.ms_recursos.service.EmpleadoService;
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

@RestController
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
@Tag(name = "Empleados", description = "Gestión del personal de Innovatech Solutions")
@CrossOrigin(origins = "*")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @PostMapping
    @Operation(summary = "Crear empleado")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Empleado creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<EmpleadoDTO> crear(@Valid @RequestBody EmpleadoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar empleados",
            description = "Sin parámetros retorna todos. Acepta filtros: ?disponibilidad=DISPONIBLE | ?rol=DESARROLLADOR | ?habilidad=Java | ?activo=true")
    public ResponseEntity<List<EmpleadoDTO>> listar(
            @Parameter(description = "Filtrar por disponibilidad: DISPONIBLE, OCUPADO, VACACIONES, LICENCIA")
            @RequestParam(required = false) DisponibilidadEstado disponibilidad,
            @Parameter(description = "Filtrar por rol: DESARROLLADOR, ARQUITECTO, GESTOR, CONSULTOR, QA, DEVOPS")
            @RequestParam(required = false) RolEmpleado rol,
            @Parameter(description = "Filtrar por habilidad (búsqueda parcial)")
            @RequestParam(required = false) String habilidad,
            @Parameter(description = "true = solo activos")
            @RequestParam(required = false) Boolean activo) {

        if (disponibilidad != null) return ResponseEntity.ok(empleadoService.obtenerPorDisponibilidad(disponibilidad));
        if (rol != null)           return ResponseEntity.ok(empleadoService.obtenerPorRol(rol));
        if (habilidad != null)     return ResponseEntity.ok(empleadoService.buscarPorHabilidad(habilidad));
        if (Boolean.TRUE.equals(activo)) return ResponseEntity.ok(empleadoService.obtenerActivos());
        return ResponseEntity.ok(empleadoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener empleado por ID")
    @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    public ResponseEntity<EmpleadoDTO> obtenerPorId(
            @Parameter(description = "ID del empleado") @PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar empleado")
    public ResponseEntity<EmpleadoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoDTO dto) {
        return ResponseEntity.ok(empleadoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar disponibilidad del empleado",
            description = "Parámetro requerido: ?disponibilidad=OCUPADO")
    public ResponseEntity<EmpleadoDTO> actualizarDisponibilidad(
            @PathVariable Long id,
            @RequestParam DisponibilidadEstado disponibilidad) {
        return ResponseEntity.ok(empleadoService.actualizarDisponibilidad(id, disponibilidad));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar empleado (baja lógica)")
    @ApiResponse(responseCode = "204", description = "Empleado eliminado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
