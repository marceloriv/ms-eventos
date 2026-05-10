package com.ticketti.ms_eventos.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.ticketti.ms_eventos.categorias.Categoria;
import com.ticketti.ms_eventos.factory.CategoriaFactory;
import com.ticketti.ms_eventos.model.Estado;
import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.model.Genero;
import com.ticketti.ms_eventos.repository.EventoRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final CategoriaFactory fabrica;
    private final EntradaProducer entradaProducer;
    private final Random random = new Random();

    /**
     * Guarda un nuevo evento.
     * fix: se podía poner más stock que aforo, ahora no.
     */
    public Evento guardarEvento(Evento evento) {
        if (evento.getStock() > evento.getAforo()) {
            throw new IllegalArgumentException("El stock no puede ser mayor al aforo");
        }
        return eventoRepository.save(evento);
    }

    /**
     * Lista todos los eventos.
     */
    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    /**
     * Actualiza un evento existente.
     * fix: se podía poner más stock que aforo, ahora no.
     */
    public Evento actualizarEvento(Evento evento) {
        if (evento.getStock() > evento.getAforo()) {
            throw new IllegalArgumentException("El stock no puede ser mayor al aforo");
        }
        return eventoRepository.save(evento);
    }

    /**
     * Elimina un evento por su ID.
     */
    public void eliminarEvento(Integer id) {
        eventoRepository.deleteById(id);
    }

    /**
     * Busca un evento por su ID.
     */
    public Optional<Evento> buscarPorId(Integer id) {
        return eventoRepository.findById(id);
    }

    /**
     * Método para que cuando se compre una entrada, se reste del stock.
     * si se agrega más stock que el aforo, tira error de stock insuficiente
     * ya que stock no puede ser mayor que aforo.
     */
    public void actualizarStock(Integer id, Integer cantidad) {
        Optional<Evento> optionalEvento = eventoRepository.findById(id);
        if (optionalEvento.isPresent()) {
            Evento evento = optionalEvento.get();
            Integer nuevoStock = evento.getStock() - cantidad;

            if (nuevoStock < 0) {
                throw new IllegalArgumentException("Stock insuficiente para realizar la operación");
            }

            evento.setStock(nuevoStock);
            eventoRepository.save(evento);
            entradaProducer.enviarEntradaComprada(evento);
        }
    }

    public String mandarCategoria(String tipo, String mensaje) {
        Categoria cat = fabrica.obtenerCategoria(tipo);
        return cat.mandarMensaje(mensaje);
    }

    @CircuitBreaker(name = "stockService", fallbackMethod = "fallbackStock")
    public String revisarStock() {
        int valor = random.nextInt(10);

        if (valor < 7) {
            throw new RuntimeException("No se logró la conexión");
        }
        return "Conexión exitosa";
    }

    public String fallbackStock(Throwable throwable) {
        return "Servicio no disponible actualmente";
    }

    // Buscar con filtros genero, nombre y ubicación.
    public List<Evento> buscarEventos(Genero genero, String nombre, String ubicacion) {
        return eventoRepository.buscarPorFiltros(genero, nombre, ubicacion);
    }

    // Método para cambiar el estado del evento, para el organizador.
    public void cambiarEstado(Integer id, String estado) {
        Optional<Evento> optionalEvento = eventoRepository.findById(id);
        if (optionalEvento.isPresent()) {
            Evento evento = optionalEvento.get();
            evento.setEstado(Estado.valueOf(estado.toUpperCase()));
            eventoRepository.save(evento);
        }
    }

}
