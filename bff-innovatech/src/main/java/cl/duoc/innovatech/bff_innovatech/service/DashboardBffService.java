package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;

import java.util.List;

public interface DashboardBffService {
    DashboardResponse obtenerDashboardConsolidado();
    List<KpiMetricaResponse> listarKpis();
    List<KpiMetricaResponse> listarKpisPorCategoria(String categoria);
}
