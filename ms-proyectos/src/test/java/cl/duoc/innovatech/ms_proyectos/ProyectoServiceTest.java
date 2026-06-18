package cl.duoc.innovatech.ms_proyectos;

import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import cl.duoc.innovatech.ms_proyectos.repository.ProyectoRepository;
import cl.duoc.innovatech.ms_proyectos.service.ProyectoService;
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
class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @InjectMocks
    private ProyectoService proyectoService;

    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        proyecto = Proyecto.builder()
                .id(1L)
                .nombre("Portal Clientes")
                .descripcion("Sistema de gestión de clientes")
                .estado("PENDIENTE")
                .avance(0)
                .responsable("Bryan Muñoz")
                .build();
    }

    @Test
    void listar_retornaLista() {
        when(proyectoRepository.findAll()).thenReturn(List.of(proyecto));
        List<Proyecto> resultado = proyectoService.listar();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Portal Clientes");
    }

    @Test
    void buscarPorId_existente_retornaProyecto() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        Optional<Proyecto> resultado = proyectoService.buscarPorId(1L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_noExistente_retornaVacio() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Proyecto> resultado = proyectoService.buscarPorId(99L);
        assertThat(resultado).isEmpty();
    }

    @Test
    void guardar_retornaProyectoGuardado() {
        when(proyectoRepository.save(proyecto)).thenReturn(proyecto);
        Proyecto resultado = proyectoService.guardar(proyecto);
        assertThat(resultado.getNombre()).isEqualTo("Portal Clientes");
        verify(proyectoRepository, times(1)).save(proyecto);
    }

    @Test
    void actualizar_existente_actualizaDatos() {
        Proyecto datosNuevos = Proyecto.builder()
                .nombre("Portal Actualizado")
                .descripcion("Descripcion nueva")
                .estado("EN_PROGRESO")
                .avance(50)
                .responsable("Karla Herrera")
                .build();

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Proyecto> resultado = proyectoService.actualizar(1L, datosNuevos);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Portal Actualizado");
        assertThat(resultado.get().getEstado()).isEqualTo("EN_PROGRESO");
        assertThat(resultado.get().getAvance()).isEqualTo(50);
    }

    @Test
    void actualizar_noExistente_retornaVacio() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Proyecto> resultado = proyectoService.actualizar(99L, proyecto);
        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(proyectoRepository.existsById(1L)).thenReturn(true);
        boolean resultado = proyectoService.eliminar(1L);
        assertThat(resultado).isTrue();
        verify(proyectoRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(proyectoRepository.existsById(99L)).thenReturn(false);
        boolean resultado = proyectoService.eliminar(99L);
        assertThat(resultado).isFalse();
        verify(proyectoRepository, never()).deleteById(any());
    }

    @Test
    void listarPorEstado_retornaFiltrados() {
        when(proyectoRepository.findByEstado("PENDIENTE")).thenReturn(List.of(proyecto));
        List<Proyecto> resultado = proyectoService.listarPorEstado("PENDIENTE");
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("PENDIENTE");
    }
}
