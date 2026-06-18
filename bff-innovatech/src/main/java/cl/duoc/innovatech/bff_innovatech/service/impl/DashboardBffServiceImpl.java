package cl.duoc.innovatech.bff_innovatech.service.impl;

import cl.duoc.innovatech.bff_innovatech.client.MsAnaliticaClient;
import cl.duoc.innovatech.bff_innovatech.client.MsRecursosClient;
import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.service.DashboardBffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio BFF del Dashboard consolidado.
 * Combina datos de ms-analitica y ms-recursos en un único DTO
 * para reducir el número de llamadas HTTP desde el frontend.
 * Esto es la esencia del patrón BFF: agregar datos de múltiples servicios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardBffServiceImpl implements DashboardBffService {

    private final MsAnaliticaClient msAnaliticaClient;
    private final MsRecursosClient  msRecursosClient;

    @Override
    public DashboardResponse obtenerDashboardConsolidado() {
        log.info("BFF: construyendo dashboard consolidado");

        // 1. Obtener datos base de analítica
        DashboardResponse analitica = msAnaliticaClient.obtenerDashboard();

        // 2. Enriquecer con resumen de recursos humanos
        ResumenRecursosResponse resumenRR = null;
        try {
            resumenRR = msRecursosClient.obtenerResumen();
        } catch (Exception ex) {
            log.warn("ms-recursos no disponible, dashboard parcial: {}", ex.getMessage());
        }

        // 3. Construir respuesta consolidada para el frontend (BFF pattern)
        if (analitica == null) {
            analitica = DashboardResponse.builder().build();
        }

        if (resumenRR != null) {
            analitica.setTotalEmpleados(resumenRR.getTotalEmpleados());
            analitica.setEmpleadosDisponibles(resumenRR.getEmpleadosDisponibles());
            analitica.setEmpleadosOcupados(resumenRR.getEmpleadosOcupados());
            analitica.setAsignacionesActivas(resumenRR.getAsignacionesActivas());
        }

        return analitica;
    }

    @Override
    public List<KpiMetricaResponse> listarKpis() {
        log.info("BFF → ms-analitica: listar KPIs");
        return msAnaliticaClient.listarKpis();
    }

    @Override
    public List<KpiMetricaResponse> listarKpisPorCategoria(String categoria) {
        log.info("BFF → ms-analitica: KPIs por categoría={}", categoria);
        return msAnaliticaClient.listarKpisPorCategoria(categoria);
    }
}
