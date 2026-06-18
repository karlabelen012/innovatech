package cl.duoc.innovatech.ms_analitica.controller;

import cl.duoc.innovatech.ms_analitica.dto.KpiMetricaDTO;
import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;
import cl.duoc.innovatech.ms_analitica.service.KpiMetricaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/kpis")
@RequiredArgsConstructor
@Tag(name = "KPI Métricas", description = "Endpoints para gestión de KPIs e indicadores")
public class KpiMetricaController {

    private final KpiMetricaService kpiService;

    @GetMapping
    @Operation(summary = "Listar todos los KPIs")
    public ResponseEntity<List<KpiMetricaDTO>> listarTodos() {
        return ResponseEntity.ok(kpiService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener KPI por ID")
    public ResponseEntity<KpiMetricaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(kpiService.obtenerPorId(id));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar KPIs por categoría")
    public ResponseEntity<List<KpiMetricaDTO>> listarPorCategoria(@PathVariable CategoriaKpi categoria) {
        return ResponseEntity.ok(kpiService.listarPorCategoria(categoria));
    }

    @GetMapping("/promedios")
    @Operation(summary = "Obtener promedios de KPIs por categoría")
    public ResponseEntity<Map<String, Double>> obtenerPromedios() {
        return ResponseEntity.ok(kpiService.obtenerPromediosPorCategoria());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo KPI")
    public ResponseEntity<KpiMetricaDTO> crear(@Valid @RequestBody KpiMetricaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kpiService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar KPI existente")
    public ResponseEntity<KpiMetricaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody KpiMetricaDTO dto) {
        return ResponseEntity.ok(kpiService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar KPI")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        kpiService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
