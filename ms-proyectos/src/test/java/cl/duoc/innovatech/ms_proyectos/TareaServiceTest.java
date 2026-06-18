package cl.duoc.innovatech.ms_proyectos;

import cl.duoc.innovatech.ms_proyectos.model.Tarea;
import cl.duoc.innovatech.ms_proyectos.repository.TareaRepository;
import cl.duoc.innovatech.ms_proyectos.service.TareaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private TareaService tareaService;

    private Tarea tarea;

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
    }

    @Test
    void listar_retornaLista() {
        when(tareaRepository.findAll()).thenReturn(List.of(tarea));
        List<Tarea> resultado = tareaService.listar();
        assertThat(resultado).hasSize(1);
    }

    @Test
    void listarPorProyecto_retornaTareas() {
        when(tareaRepository.findByProyectoId(1L)).thenReturn(List.of(tarea));
        List<Tarea> resultado = tareaService.listarPorProyecto(1L);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProyectoId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_existente_retornaTarea() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        Optional<Tarea> resultado = tareaService.buscarPorId(1L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Diseñar base de datos");
    }

    @Test
    void buscarPorId_noExistente_retornaVacio() {
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Tarea> resultado = tareaService.buscarPorId(99L);
        assertThat(resultado).isEmpty();
    }

    @Test
    void guardar_retornaTareaGuardada() {
        when(tareaRepository.save(tarea)).thenReturn(tarea);
        Tarea resultado = tareaService.guardar(tarea);
        assertThat(resultado.getTitulo()).isEqualTo("Diseñar base de datos");
        verify(tareaRepository).save(tarea);
    }

    @Test
    void actualizar_existente_actualizaDatos() {
        Tarea datosNuevos = Tarea.builder()
                .titulo("Implementar API")
                .descripcion("Crear endpoints REST")
                .estado("EN_PROGRESO")
                .responsable("Karla Herrera")
                .proyectoId(1L)
                .build();

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Tarea> resultado = tareaService.actualizar(1L, datosNuevos);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Implementar API");
        assertThat(resultado.get().getEstado()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void actualizar_noExistente_retornaVacio() {
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Tarea> resultado = tareaService.actualizar(99L, tarea);
        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(tareaRepository.existsById(1L)).thenReturn(true);
        boolean resultado = tareaService.eliminar(1L);
        assertThat(resultado).isTrue();
        verify(tareaRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(tareaRepository.existsById(99L)).thenReturn(false);
        boolean resultado = tareaService.eliminar(99L);
        assertThat(resultado).isFalse();
        verify(tareaRepository, never()).deleteById(any());
    }
}
