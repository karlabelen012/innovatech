package cl.duoc.innovatech.ms_proyectos.service;

import cl.duoc.innovatech.ms_proyectos.dto.TareaDTO;
import cl.duoc.innovatech.ms_proyectos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_proyectos.model.Tarea;
import cl.duoc.innovatech.ms_proyectos.repository.TareaRepository;
import cl.duoc.innovatech.ms_proyectos.service.impl.TareaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private TareaServiceImpl tareaService;

    private Tarea tarea;
    private TareaDTO tareaDTO;

    @BeforeEach
    void setUp() {
        tarea = Tarea.builder()
                .id(1L)
                .titulo("Diseñar base de datos")
                .descripcion("Crear el modelo ER")
                .estado("PENDIENTE")
                .responsable("Bryan Muñoz")
                .proyectoId(1L)
                .build();

        tareaDTO = TareaDTO.builder()
                .titulo("Diseñar base de datos")
                .descripcion("Crear el modelo ER")
                .estado("PENDIENTE")
                .responsable("Bryan Muñoz")
                .proyectoId(1L)
                .build();
    }

    @Test
    void listar_retornaLista() {
        when(tareaRepository.findAll()).thenReturn(List.of(tarea));
        List<TareaDTO> resultado = tareaService.listar();
        assertThat(resultado).hasSize(1);
    }

    @Test
    void listarPorProyecto_retornaTareas() {
        when(tareaRepository.findByProyectoId(1L)).thenReturn(List.of(tarea));
        List<TareaDTO> resultado = tareaService.listarPorProyecto(1L);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProyectoId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_existente_retornaTarea() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        TareaDTO resultado = tareaService.obtenerPorId(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Diseñar base de datos");
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tareaService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void crear_retornaTareaGuardada() {
        when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);
        TareaDTO resultado = tareaService.crear(tareaDTO);
        assertThat(resultado.getTitulo()).isEqualTo("Diseñar base de datos");
        verify(tareaRepository).save(any(Tarea.class));
    }

    @Test
    void actualizar_existente_actualizaDatos() {
        TareaDTO datosNuevos = TareaDTO.builder()
                .titulo("Implementar API")
                .descripcion("Crear endpoints REST")
                .estado("EN_PROGRESO")
                .responsable("Karla Herrera")
                .proyectoId(1L)
                .build();

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(i -> i.getArgument(0));

        TareaDTO resultado = tareaService.actualizar(1L, datosNuevos);
        assertThat(resultado.getTitulo()).isEqualTo("Implementar API");
        assertThat(resultado.getEstado()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tareaService.actualizar(99L, tareaDTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminar_existente_eliminaTarea() {
        when(tareaRepository.existsById(1L)).thenReturn(true);
        tareaService.eliminar(1L);
        verify(tareaRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(tareaRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> tareaService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(tareaRepository, never()).deleteById(any());
    }
}
