package com.ticketti.ms_eventos.service;

import com.ticketti.ms_eventos.model.Estado;
import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.model.Genero;
import com.ticketti.ms_eventos.repository.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private EntradaProducer entradaProducer;

    @InjectMocks
    private EventoService eventoService;

    private Evento eventoValido;

    @BeforeEach
    void setUp() {
        eventoValido = new Evento();
        eventoValido.setId(1);
        eventoValido.setNombre("Lollapalooza");
        eventoValido.setDescripcion("Festival de música");
        eventoValido.setGenero(Genero.ROCK);
        eventoValido.setEstado(Estado.PUBLICADO);
        eventoValido.setAforo(100);
        eventoValido.setStock(50);
        eventoValido.setPrecioEntrada(15000.0);
    }

    // ---------- guardarEvento ----------

    @Test
    void guardarEvento_stockMenorQueAforo_guardaCorrectamente() {
        when(eventoRepository.save(eventoValido)).thenReturn(eventoValido);

        Evento resultado = eventoService.guardarEvento(eventoValido);

        assertNotNull(resultado);
        assertEquals("Lollapalooza", resultado.getNombre());
        verify(eventoRepository, times(1)).save(eventoValido);
    }

    @Test
    void guardarEvento_stockMayorQueAforo_lanzaExcepcion() {
        eventoValido.setStock(200); // mayor que aforo (100)

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> eventoService.guardarEvento(eventoValido));

        assertEquals("El stock no puede ser mayor al aforo", ex.getMessage());
        verify(eventoRepository, never()).save(any());
    }

    // ---------- listarEventos ----------

    @Test
    void listarEventos_retornaListaDeEventos() {
        when(eventoRepository.findAll()).thenReturn(List.of(eventoValido));

        List<Evento> resultado = eventoService.listarEventos();

        assertEquals(1, resultado.size());
        verify(eventoRepository, times(1)).findAll();
    }

    // ---------- listarPorOrganizador ----------

    @Test
    void listarPorOrganizador_idNull_retornaListaVacia() {
        List<Evento> resultado = eventoService.listarPorOrganizador(null);

        assertTrue(resultado.isEmpty());
        verify(eventoRepository, never()).findByOrganizadorId(any());
    }

    @Test
    void listarPorOrganizador_idValido_retornaEventosDelOrganizador() {
        eventoValido.setOrganizadorId(5L);
        when(eventoRepository.findByOrganizadorId(5L)).thenReturn(List.of(eventoValido));

        List<Evento> resultado = eventoService.listarPorOrganizador(5L);

        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getOrganizadorId());
    }

    // ---------- actualizarEvento ----------

    @Test
    void actualizarEvento_stockMayorQueAforo_lanzaExcepcion() {
        eventoValido.setStock(150);

        assertThrows(IllegalArgumentException.class,
                () -> eventoService.actualizarEvento(eventoValido));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void actualizarEvento_valido_actualizaCorrectamente() {
        when(eventoRepository.save(eventoValido)).thenReturn(eventoValido);

        Evento resultado = eventoService.actualizarEvento(eventoValido);

        assertNotNull(resultado);
        verify(eventoRepository, times(1)).save(eventoValido);
    }

    // ---------- eliminarEvento ----------

    @Test
    void eliminarEvento_llamaAlRepository() {
        eventoService.eliminarEvento(1);
        verify(eventoRepository, times(1)).deleteById(1);
    }

    // ---------- buscarPorId ----------

    @Test
    void buscarPorId_existente_retornaEvento() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(eventoValido));

        Optional<Evento> resultado = eventoService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Lollapalooza", resultado.get().getNombre());
    }

    @Test
    void buscarPorId_noExistente_retornaVacio() {
        when(eventoRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Evento> resultado = eventoService.buscarPorId(99);

        assertTrue(resultado.isEmpty());
    }

    // ---------- actualizarStock ----------

    @Test
    void actualizarStock_stockSuficiente_descuentaYPublicaMensaje() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(eventoValido));
        when(eventoRepository.save(any())).thenReturn(eventoValido);

        eventoService.actualizarStock(1, 10);

        assertEquals(40, eventoValido.getStock());
        verify(eventoRepository, times(1)).save(eventoValido);
        verify(entradaProducer, times(1)).enviarEntradaComprada(eventoValido);
    }

    @Test
    void actualizarStock_stockInsuficiente_lanzaExcepcion() {
        eventoValido.setStock(5);
        when(eventoRepository.findById(1)).thenReturn(Optional.of(eventoValido));

        assertThrows(IllegalArgumentException.class,
                () -> eventoService.actualizarStock(1, 10));

        verify(eventoRepository, never()).save(any());
        verify(entradaProducer, never()).enviarEntradaComprada(any());
    }

    @Test
    void actualizarStock_eventoNoExiste_noHaceNada() {
        when(eventoRepository.findById(99)).thenReturn(Optional.empty());

        eventoService.actualizarStock(99, 10);

        verify(eventoRepository, never()).save(any());
        verify(entradaProducer, never()).enviarEntradaComprada(any());
    }

    // ---------- restaurarStock ----------

    @Test
    void restaurarStock_eventoExistente_aumentaStock() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(eventoValido));
        when(eventoRepository.save(any())).thenReturn(eventoValido);

        eventoService.restaurarStock(1, 5);

        assertEquals(55, eventoValido.getStock());
        verify(eventoRepository, times(1)).save(eventoValido);
    }

    // ---------- revisarStock / fallback ----------

    @Test
    void fallbackStock_retornaMensajeDeServicioNoDisponible() {
        String resultado = eventoService.fallbackStock(new RuntimeException("falla simulada"));

        assertEquals("Servicio no disponible actualmente", resultado);
    }

    // ---------- buscarEventos ----------

    @Test
    void buscarEventos_conFiltros_retornaResultadosDelRepository() {
        when(eventoRepository.buscarPorFiltros(Genero.ROCK, "Lolla", "Santiago"))
                .thenReturn(List.of(eventoValido));

        List<Evento> resultado = eventoService.buscarEventos(Genero.ROCK, "Lolla", "Santiago");

        assertEquals(1, resultado.size());
        verify(eventoRepository, times(1)).buscarPorFiltros(Genero.ROCK, "Lolla", "Santiago");
    }

    // ---------- cambiarEstado ----------

    @Test
    void cambiarEstado_eventoExistente_actualizaEstado() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(eventoValido));
        when(eventoRepository.save(any())).thenReturn(eventoValido);

        eventoService.cambiarEstado(1, "CANCELADO");

        assertEquals(Estado.CANCELADO, eventoValido.getEstado());
        verify(eventoRepository, times(1)).save(eventoValido);
    }

    @Test
    void cambiarEstado_estadoInvalido_lanzaExcepcion() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(eventoValido));

        assertThrows(IllegalArgumentException.class,
                () -> eventoService.cambiarEstado(1, "NO_EXISTE"));
    }

    @Test
    void cambiarEstado_eventoNoExiste_noHaceNada() {
        when(eventoRepository.findById(99)).thenReturn(Optional.empty());

        eventoService.cambiarEstado(99, "PUBLICADO");

        verify(eventoRepository, never()).save(any());
    }
}