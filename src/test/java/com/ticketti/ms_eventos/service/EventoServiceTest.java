package com.ticketti.ms_eventos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ticketti.ms_eventos.repository.EventoRepository;
import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.factory.CategoriaFactory;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private EntradaProducer entradaProducer;

    @Mock
    private CategoriaFactory fabrica;

    @InjectMocks
    private EventoService eventoService;

    @Test
    void guardarEvento_stockMayorQueAforo_lanzaExcepcion() {
        Evento evento = new Evento();
        evento.setAforo(100);
        evento.setStock(200);

        assertThrows(IllegalArgumentException.class,
            () -> eventoService.guardarEvento(evento));
    }

    @Test
    void guardarEvento_stockValido_guardaCorrectamente() {
        Evento evento = new Evento();
        evento.setAforo(100);
        evento.setStock(50);

        when(eventoRepository.save(evento)).thenReturn(evento);

        Evento resultado = eventoService.guardarEvento(evento);
        assertNotNull(resultado);
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    void actualizarStock_stockInsuficiente_lanzaExcepcion() {
        Evento evento = new Evento();
        evento.setStock(5);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));

        assertThrows(IllegalArgumentException.class,
            () -> eventoService.actualizarStock(1, 10));
    }
}