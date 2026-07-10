package cl.duoc.innovatech.ms_mensajeria.config;

import cl.duoc.innovatech.ms_mensajeria.repository.MensajeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensajeriaDataSeederTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @InjectMocks
    private MensajeriaDataSeeder seeder;

    @Test
    void shouldSeedDataWhenDatabaseIsEmpty() {
        when(mensajeRepository.count()).thenReturn(0L);
        when(mensajeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        seeder.run();

        verify(mensajeRepository).saveAll(anyList());
    }

    @Test
    void shouldSkipSeedWhenDatabaseAlreadyHasData() {
        when(mensajeRepository.count()).thenReturn(1L);

        seeder.run();

        verify(mensajeRepository, never()).saveAll(anyList());
    }
}
