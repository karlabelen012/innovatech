package cl.duoc.innovatech.ms_analitica.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.innovatech.ms_analitica.dto.KpiMetricaDTO;
import cl.duoc.innovatech.ms_analitica.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_analitica.model.KpiMetrica;
import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;
import cl.duoc.innovatech.ms_analitica.repository.KpiMetricaRepository;
import cl.duoc.innovatech.ms_analitica.service.impl.KpiMetricaServiceImpl;

@ExtendWith(MockitoExtension.class)
class KpiMetricaServiceTest {

    @Mock
    private KpiMetricaRepository kpiRepository;

    @InjectMocks
    private KpiMetricaServiceImpl kpiService;

    private KpiMetrica kpi;
    private KpiMetricaDTO kpiDTO;

    @BeforeEach
    void setUp() {
        kpi = KpiMetrica.builder()
                .id(1L)
                .nombreKpi("Proyectos Activos")
                .categoria(CategoriaKpi.PROYECTOS)
                .valor(5.0)
                .unidad("proyectos")
                .descripcion("Cantidad de proyectos en progreso")
                .build();

        kpiDTO = KpiMetricaDTO.builder()
                .nombreKpi("Proyectos Activos")
                .categoria(CategoriaKpi.PROYECTOS)
                .valor(5.0)
                .unidad("proyectos")
                .descripcion("Cantidad de proyectos en progreso")
                .build();
    }

    @Test
    void listarTodos_debeRetornarLista() {
        when(kpiRepository.findAll()).thenReturn(List.of(kpi));
        List<KpiMetricaDTO> resultado = kpiService.listarTodos();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreKpi()).isEqualTo("Proyectos Activos");
    }

    @Test
    void listarTodos_listaVacia_debeRetornarListaVacia() {
        when(kpiRepository.findAll()).thenReturn(List.of());
        List<KpiMetricaDTO> resultado = kpiService.listarTodos();
        assertThat(resultado).isEmpty();
    }

    @Test
    void obtenerPorId_existente_debeRetornarDTO() {
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));
        KpiMetricaDTO resultado = kpiService.obtenerPorId(1L);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombreKpi()).isEqualTo("Proyectos Activos");
    }

    @Test
    void obtenerPorId_noExistente_debeLanzarExcepcion() {
        when(kpiRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> kpiService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void crear_debeRetornarKpiCreado() {
        when(kpiRepository.save(any(KpiMetrica.class))).thenReturn(kpi);
        KpiMetricaDTO resultado = kpiService.crear(kpiDTO);
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(kpiRepository).save(any(KpiMetrica.class));
    }

    @Test
    void actualizar_existente_debeActualizar() {
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));
        when(kpiRepository.save(any(KpiMetrica.class))).thenReturn(kpi);
        KpiMetricaDTO resultado = kpiService.actualizar(1L, kpiDTO);
        assertThat(resultado).isNotNull();
        verify(kpiRepository).save(any(KpiMetrica.class));
    }

    @Test
    void actualizar_noExistente_debeLanzarExcepcion() {
        when(kpiRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> kpiService.actualizar(99L, kpiDTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void eliminar_existente_debeEliminar() {
        when(kpiRepository.existsById(1L)).thenReturn(true);
        kpiService.eliminar(1L);
        verify(kpiRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_debeLanzarExcepcion() {
        when(kpiRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> kpiService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listarPorCategoria_debeRetornarFiltrados() {
        when(kpiRepository.findByCategoria(CategoriaKpi.PROYECTOS)).thenReturn(List.of(kpi));
        List<KpiMetricaDTO> resultado = kpiService.listarPorCategoria(CategoriaKpi.PROYECTOS);
        assertThat(resultado).hasSize(1);
    }

    @Test
    void obtenerPromediosPorCategoria_debeRetornarMapa() {
        Object[] fila = new Object[]{CategoriaKpi.PROYECTOS, 7.5};
        List<Object[]> filas = new java.util.ArrayList<>();
        filas.add(fila);
        when(kpiRepository.promedioValorPorCategoria()).thenReturn(filas);
        Map<String, Double> resultado = kpiService.obtenerPromediosPorCategoria();
        assertThat(resultado).containsKey("PROYECTOS");
        assertThat(resultado.get("PROYECTOS")).isEqualTo(7.5);
    }

    @Test
    void obtenerPromediosPorCategoria_sinDatos_debeRetornarMapaVacio() {
        when(kpiRepository.promedioValorPorCategoria()).thenReturn(List.of());
        Map<String, Double> resultado = kpiService.obtenerPromediosPorCategoria();
        assertThat(resultado).isEmpty();
    }
}