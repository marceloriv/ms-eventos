package com.ticketti.ms_eventos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.repository.EventoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EventoService {
    @Autowired
    private EventoRepository eventoRepository;

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
}
