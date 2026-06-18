package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import cl.duoc.innovatech.bff_innovatech.service.DashboardBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador BFF del Dashboard.
 * Este endpoint es el más representativo del patrón BFF:
 * consolida datos de ms-analitica y ms-recursos en una sola respuesta
 * para que el frontend no deba hacer múltiples llamadas.
 */
@RestController
@RequestMapping("/api/bff/dashboard")
@RequiredArgsConstructor
@Tag(name = "BFF – Dashboard", description = "Panel consolidado: agrega datos de ms-analitica y ms-recursos")
@CrossOrigin(origins = "*")
public class DashboardBffController {

    private final DashboardBffService dashboardBffService;

    @GetMapping
    @Operation(summary = "Dashboard consolidado",
               description = "Combina KPIs de ms-analitica con resumen de recursos de ms-recursos en una sola respuesta optimizada para el frontend.")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(dashboardBffService.obtenerDashboardConsolidado());
    }

    @GetMapping("/kpis")
    @Operation(summary = "Listar todos los KPIs")
    public ResponseEntity<List<KpiMetricaResponse>> kpis() {
        return ResponseEntity.ok(dashboardBffService.listarKpis());
    }

    @GetMapping("/kpis/categoria")
    @Operation(summary = "KPIs por categoría",
               description = "Parámetro: ?categoria=PROYECTOS | RECURSOS | PRODUCTIVIDAD | FINANCIERO")
    public ResponseEntity<List<KpiMetricaResponse>> kpisPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(dashboardBffService.listarKpisPorCategoria(categoria));
    }
}
