package com.ticketti.ms_eventos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.model.Genero;
import com.ticketti.ms_eventos.service.EventoService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador REST para operaciones CRUD de Eventos.
 */
@RestController
@RequestMapping("/api/v0/Eventos")
@RequiredArgsConstructor
public class EventoController {

    private EventoService eventoService;

    /**
     * Guarda un nuevo evento.
     */
    @PostMapping("/crear")
    public ResponseEntity<Evento> save(@RequestBody Evento evento) {
        Evento nuevoEvento = eventoService.guardarEvento(evento);
        return ResponseEntity.ok(nuevoEvento);
    }

    /**
     * Lista todos los eventos.
     */
    @GetMapping("/listarEventos")
    public ResponseEntity<List<Evento>> findAll() {
        List<Evento> eventos = eventoService.listarEventos();
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }

    /**
     * Actualiza un evento existente.
     */
    @PatchMapping("/actualizarEvento/{id}")
    public ResponseEntity<Evento> updateEvento(Evento evento) {
        Evento nuevoEvento = eventoService.actualizarEvento(evento);
        return ResponseEntity.ok(nuevoEvento);
    }

    /**
     * Elimina un evento por su ID.
     */
    @DeleteMapping("/eliminarEvento/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca un evento por su ID.
     */
    @GetMapping("/buscarEvento/{id}")
    public ResponseEntity<Evento> findById(@PathVariable Integer id) {
        return eventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Método para que cuando se compre una entrada, se reste del stock.
     * Este método es consumido por RabbitMQ y por lógica.
     */
    @PatchMapping("/actualizarStock/{id}/{cantidad}")
    public ResponseEntity<Void> actualizarStock(@PathVariable Integer id, @PathVariable Integer cantidad) {
        eventoService.actualizarStock(id, cantidad);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categoria/{tipo}")
    public String mandarCategoria(@PathVariable String tipo, @RequestBody String mensaje) {
        return eventoService.mandarCategoria(tipo, mensaje);
    }

    @GetMapping("/stock/{check}")
    public String revisarStock() {
        return eventoService.revisarStock();
    }

    // GET para busacr por genero, nombre, categoría y ubicación.
    @GetMapping("/buscar")
    public ResponseEntity<List<Evento>> buscar(
            @RequestParam(required = false) Genero genero,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ubicacion) {
        List<Evento> eventos = eventoService.buscarEventos(genero, nombre, ubicacion);
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }

    // PATCH para cambiar el estado del evento.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam String estado) {
        eventoService.cambiarEstado(id, estado);
        return ResponseEntity.noContent().build();
    }
}