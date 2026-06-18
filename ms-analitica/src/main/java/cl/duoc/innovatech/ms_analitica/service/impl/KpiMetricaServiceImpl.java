package cl.duoc.innovatech.ms_analitica.service.impl;

import cl.duoc.innovatech.ms_analitica.dto.KpiMetricaDTO;
import cl.duoc.innovatech.ms_analitica.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_analitica.model.KpiMetrica;
import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;
import cl.duoc.innovatech.ms_analitica.repository.KpiMetricaRepository;
import cl.duoc.innovatech.ms_analitica.service.KpiMetricaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KpiMetricaServiceImpl implements KpiMetricaService {

    private final KpiMetricaRepository kpiRepository;

    @Override
    public List<KpiMetricaDTO> listarTodos() {
        return kpiRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public KpiMetricaDTO obtenerPorId(Long id) {
        return toDTO(kpiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("KPI no encontrado con id: " + id)));
    }

    @Override
    public KpiMetricaDTO crear(KpiMetricaDTO dto) {
        return toDTO(kpiRepository.save(toEntity(dto)));
    }

    @Override
    public KpiMetricaDTO actualizar(Long id, KpiMetricaDTO dto) {
        KpiMetrica existente = kpiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("KPI no encontrado con id: " + id));
        existente.setNombreKpi(dto.getNombreKpi());
        existente.setCategoria(dto.getCategoria());
        existente.setValor(dto.getValor());
        existente.setUnidad(dto.getUnidad());
        existente.setDescripcion(dto.getDescripcion());
        return toDTO(kpiRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        if (!kpiRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("KPI no encontrado con id: " + id);
        }
        kpiRepository.deleteById(id);
    }

    @Override
    public List<KpiMetricaDTO> listarPorCategoria(CategoriaKpi categoria) {
        return kpiRepository.findByCategoria(categoria).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Double> obtenerPromediosPorCategoria() {
        List<Object[]> resultados = kpiRepository.promedioValorPorCategoria();
        Map<String, Double> mapa = new HashMap<>();
        for (Object[] fila : resultados) {
            mapa.put(fila[0].toString(), (Double) fila[1]);
        }
        return mapa;
    }

    private KpiMetricaDTO toDTO(KpiMetrica e) {
        return KpiMetricaDTO.builder()
                .id(e.getId())
                .nombreKpi(e.getNombreKpi())
                .categoria(e.getCategoria())
                .valor(e.getValor())
                .unidad(e.getUnidad())
                .descripcion(e.getDescripcion())
                .build();
    }

    private KpiMetrica toEntity(KpiMetricaDTO dto) {
        return KpiMetrica.builder()
                .nombreKpi(dto.getNombreKpi())
                .categoria(dto.getCategoria())
                .valor(dto.getValor())
                .unidad(dto.getUnidad())
                .descripcion(dto.getDescripcion())
                .build();
    }
}
