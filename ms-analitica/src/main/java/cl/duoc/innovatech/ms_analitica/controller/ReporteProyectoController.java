package cl.duoc.innovatech.ms_analitica.controller;

import cl.duoc.innovatech.ms_analitica.dto.ReporteProyectoDTO;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import cl.duoc.innovatech.ms_analitica.service.ReporteProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes de Proyecto", description = "Endpoints para gestión de reportes de proyectos")
public class ReporteProyectoController {

    private final ReporteProyectoService reporteService;

    @GetMapping
    @Operation(summary = "Listar todos los reportes de proyecto")
    public ResponseEntity<List<ReporteProyectoDTO>> listarTodos() {
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reporte por ID")
    public ResponseEntity<ReporteProyectoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    @GetMapping("/externo/{proyectoIdExterno}")
    @Operation(summary = "Obtener reporte por ID externo del proyecto")
    public ResponseEntity<ReporteProyectoDTO> obtenerPorProyectoExterno(@PathVariable Long proyectoIdExterno) {
        return ResponseEntity.ok(reporteService.obtenerPorProyectoExterno(proyectoIdExterno));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar reportes por estado")
    public ResponseEntity<List<ReporteProyectoDTO>> listarPorEstado(@PathVariable EstadoProyecto estado) {
        return ResponseEntity.ok(reporteService.listarPorEstado(estado));
    }

    @GetMapping("/promedio-avance")
    @Operation(summary = "Calcular promedio de avance de proyectos activos")
    public ResponseEntity<Double> calcularPromedioAvance() {
        return ResponseEntity.ok(reporteService.calcularPromedioAvance());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo reporte de proyecto")
    public ResponseEntity<ReporteProyectoDTO> crear(@Valid @RequestBody ReporteProyectoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reporte de proyecto")
    public ResponseEntity<ReporteProyectoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReporteProyectoDTO dto) {
        return ResponseEntity.ok(reporteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reporte de proyecto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
