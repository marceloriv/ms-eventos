package com.ticketti.ms_eventos.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticketti.ms_eventos.categorias.Categoria;
import com.ticketti.ms_eventos.factory.CategoriaFactory;
import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.repository.EventoRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@AllArgsConstructor
public class EventoService {
    @Autowired
    private EventoRepository eventoRepository;
    private final CategoriaFactory fabrica;
    private final Random random = new Random();

    /**
     * Guarda un nuevo evento.
     */
    public Evento guardarEvento(Evento evento) {
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
     */
    public Evento actualizarEvento(Evento evento) {
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
     */
    public void actualizarStock(Integer id, Integer cantidad) {
        Optional<Evento> optionalEvento = eventoRepository.findById(id);
        if (optionalEvento.isPresent()) {
            Evento evento = optionalEvento.get();
            Integer nuevoStock = evento.getStock() - cantidad;
            evento.setStock(nuevoStock);
            eventoRepository.save(evento);
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
}
