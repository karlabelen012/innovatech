package cl.duoc.innovatech.ms_analitica.service;

import cl.duoc.innovatech.ms_analitica.dto.KpiMetricaDTO;
import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;

import java.util.List;
import java.util.Map;

public interface KpiMetricaService {
    List<KpiMetricaDTO> listarTodos();
    KpiMetricaDTO obtenerPorId(Long id);
    KpiMetricaDTO crear(KpiMetricaDTO dto);
    KpiMetricaDTO actualizar(Long id, KpiMetricaDTO dto);
    void eliminar(Long id);
    List<KpiMetricaDTO> listarPorCategoria(CategoriaKpi categoria);
    Map<String, Double> obtenerPromediosPorCategoria();
}
