package cl.duoc.innovatech.ms_analitica.service;

import java.util.List;
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

import cl.duoc.innovatech.ms_analitica.dto.ReporteProyectoDTO;
import cl.duoc.innovatech.ms_analitica.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_analitica.model.ReporteProyecto;
import cl.duoc.innovatech.ms_analitica.model.enums.EstadoProyecto;
import cl.duoc.innovatech.ms_analitica.repository.ReporteProyectoRepository;
import cl.duoc.innovatech.ms_analitica.service.impl.ReporteProyectoServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReporteProyectoServiceTest {

    @Mock
    private ReporteProyectoRepository reporteRepository;

    @InjectMocks
    private ReporteProyectoServiceImpl reporteService;

    private ReporteProyecto reporte;
    private ReporteProyectoDTO reporteDTO;

    @BeforeEach
    void setUp() {
        reporte = ReporteProyecto.builder()
                .id(1L)
                .nombreProyecto("Proyecto Alpha")
                .proyectoIdExterno(10L)
                .estado(EstadoProyecto.EN_PROGRESO)
                .porcentajeAvance(60)
                .totalTareas(10)
                .tareasCompletadas(6)
                .tareasPendientes(4)
                .recursosAsignados(3)
                .build();

        reporteDTO = ReporteProyectoDTO.builder()
                .nombreProyecto("Proyecto Alpha")
                .proyectoIdExterno(10L)
                .estado(EstadoProyecto.EN_PROGRESO)
                .porcentajeAvance(60)
                .totalTareas(10)
                .tareasCompletadas(6)
                .tareasPendientes(4)
                .recursosAsignados(3)
                .build();
    }

    @Test
    void listarTodos_debeRetornarLista() {
        when(reporteRepository.findAll()).thenReturn(List.of(reporte));
        List<ReporteProyectoDTO> resultado = reporteService.listarTodos();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreProyecto()).isEqualTo("Proyecto Alpha");
    }

    @Test
    void obtenerPorId_existente_debeRetornarDTO() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        ReporteProyectoDTO resultado = reporteService.obtenerPorId(1L);
        assertThat(resultado.getNombreProyecto()).isEqualTo("Proyecto Alpha");
    }

    @Test
    void obtenerPorId_noExistente_debeLanzarExcepcion() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reporteService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtenerPorProyectoExterno_existente_debeRetornarDTO() {
        when(reporteRepository.findByProyectoIdExterno(10L)).thenReturn(Optional.of(reporte));
        ReporteProyectoDTO resultado = reporteService.obtenerPorProyectoExterno(10L);
        assertThat(resultado.getProyectoIdExterno()).isEqualTo(10L);
    }

    @Test
    void obtenerPorProyectoExterno_noExistente_debeLanzarExcepcion() {
        when(reporteRepository.findByProyectoIdExterno(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reporteService.obtenerPorProyectoExterno(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void crear_debeRetornarReporteCreado() {
        when(reporteRepository.save(any(ReporteProyecto.class))).thenReturn(reporte);
        ReporteProyectoDTO resultado = reporteService.crear(reporteDTO);
        assertThat(resultado.getNombreProyecto()).isEqualTo("Proyecto Alpha");
        verify(reporteRepository).save(any(ReporteProyecto.class));
    }

    @Test
    void actualizar_existente_debeRetornarActualizado() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        when(reporteRepository.save(any(ReporteProyecto.class))).thenReturn(reporte);
        ReporteProyectoDTO resultado = reporteService.actualizar(1L, reporteDTO);
        assertThat(resultado).isNotNull();
        verify(reporteRepository).save(any(ReporteProyecto.class));
    }

    @Test
    void actualizar_noExistente_debeLanzarExcepcion() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reporteService.actualizar(99L, reporteDTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminar_existente_debeEliminar() {
        when(reporteRepository.existsById(1L)).thenReturn(true);
        reporteService.eliminar(1L);
        verify(reporteRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_debeLanzarExcepcion() {
        when(reporteRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> reporteService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void listarPorEstado_debeRetornarFiltrados() {
        when(reporteRepository.findByEstado(EstadoProyecto.EN_PROGRESO)).thenReturn(List.of(reporte));
        List<ReporteProyectoDTO> resultado = reporteService.listarPorEstado(EstadoProyecto.EN_PROGRESO);
        assertThat(resultado).hasSize(1);
    }

    @Test
    void calcularPromedioAvance_conDatos_debeRetornarValor() {
        when(reporteRepository.calcularPromedioAvanceActivos()).thenReturn(65.5);
        Double resultado = reporteService.calcularPromedioAvance();
        assertThat(resultado).isEqualTo(65.5);
    }

    @Test
    void calcularPromedioAvance_sinDatos_debeRetornarCero() {
        when(reporteRepository.calcularPromedioAvanceActivos()).thenReturn(null);
        Double resultado = reporteService.calcularPromedioAvance();
        assertThat(resultado).isEqualTo(0.0);
    }
}