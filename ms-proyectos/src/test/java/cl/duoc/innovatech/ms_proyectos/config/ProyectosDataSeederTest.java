package cl.duoc.innovatech.ms_proyectos.config;

import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import cl.duoc.innovatech.ms_proyectos.repository.ProyectoRepository;
import cl.duoc.innovatech.ms_proyectos.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectosDataSeederTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private ProyectosDataSeeder seeder;

    @Test
    void shouldSeedDataWhenDatabaseIsEmpty() {
        when(proyectoRepository.count()).thenReturn(0L);
        when(tareaRepository.count()).thenReturn(0L);
        when(proyectoRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Proyecto> proyectos = invocation.getArgument(0);
            long id = 1L;
            for (Proyecto p : proyectos) {
                p.setId(id++);
            }
            return proyectos;
        });
        when(tareaRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        seeder.run();

        verify(proyectoRepository).saveAll(anyList());
        verify(tareaRepository).saveAll(anyList());
    }

    @Test
    void shouldSkipSeedWhenDatabaseAlreadyHasData() {
        when(proyectoRepository.count()).thenReturn(1L);

        seeder.run();

        verify(proyectoRepository, never()).saveAll(anyList());
        verify(tareaRepository, never()).saveAll(anyList());
    }
}
