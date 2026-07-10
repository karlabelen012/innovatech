package cl.duoc.innovatech.ms_recursos.config;

import cl.duoc.innovatech.ms_recursos.repository.AsignacionRepository;
import cl.duoc.innovatech.ms_recursos.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecursosDataSeederTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private AsignacionRepository asignacionRepository;

    @InjectMocks
    private RecursosDataSeeder seeder;

    @Test
    void shouldSeedDataWhenDatabaseIsEmpty() {
        when(empleadoRepository.count()).thenReturn(0L);
        when(asignacionRepository.count()).thenReturn(0L);
        when(empleadoRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(asignacionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        seeder.run();

        verify(empleadoRepository).saveAll(anyList());
        verify(asignacionRepository).saveAll(anyList());
    }
}
