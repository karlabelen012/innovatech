package cl.duoc.innovatech.ms_proyectos.service;

import cl.duoc.innovatech.ms_proyectos.dto.ProyectoDTO;
import cl.duoc.innovatech.ms_proyectos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import cl.duoc.innovatech.ms_proyectos.repository.ProyectoRepository;
import cl.duoc.innovatech.ms_proyectos.service.impl.ProyectoServiceImpl;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @InjectMocks
    private ProyectoServiceImpl proyectoService;

    private Proyecto proyecto;
    private ProyectoDTO proyectoDTO;

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

        proyectoDTO = ProyectoDTO.builder()
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
        List<ProyectoDTO> resultado = proyectoService.listar();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Portal Clientes");
    }

    @Test
    void obtenerPorId_existente_retornaProyecto() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        ProyectoDTO resultado = proyectoService.obtenerPorId(1L);
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> proyectoService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void crear_retornaProyectoGuardado() {
        when(proyectoRepository.save(any(Proyecto.class))).thenReturn(proyecto);
        ProyectoDTO resultado = proyectoService.crear(proyectoDTO);
        assertThat(resultado.getNombre()).isEqualTo("Portal Clientes");
        verify(proyectoRepository, times(1)).save(any(Proyecto.class));
    }

    @Test
    void actualizar_existente_actualizaDatos() {
        ProyectoDTO datosNuevos = ProyectoDTO.builder()
                .nombre("Portal Actualizado")
                .descripcion("Descripcion nueva")
                .estado("EN_PROGRESO")
                .avance(50)
                .responsable("Karla Herrera")
                .build();

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(i -> i.getArgument(0));

        ProyectoDTO resultado = proyectoService.actualizar(1L, datosNuevos);
        assertThat(resultado.getNombre()).isEqualTo("Portal Actualizado");
        assertThat(resultado.getEstado()).isEqualTo("EN_PROGRESO");
        assertThat(resultado.getAvance()).isEqualTo(50);
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> proyectoService.actualizar(99L, proyectoDTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminar_existente_eliminaProyecto() {
        when(proyectoRepository.existsById(1L)).thenReturn(true);
        proyectoService.eliminar(1L);
        verify(proyectoRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(proyectoRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> proyectoService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(proyectoRepository, never()).deleteById(any());
    }

    @Test
    void listarPorEstado_retornaFiltrados() {
        when(proyectoRepository.findByEstado("PENDIENTE")).thenReturn(List.of(proyecto));
        List<ProyectoDTO> resultado = proyectoService.listarPorEstado("PENDIENTE");
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("PENDIENTE");
    }
}
