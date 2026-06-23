package com.ticketti.ms_eventos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.model.Genero;
import com.ticketti.ms_eventos.service.EventoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para operaciones CRUD de Eventos.
 */
@Slf4j
@RestController
@RequestMapping("/api/v0/Eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    /**
     * Guarda un nuevo evento.
     */
    @PostMapping("/crear")
    public ResponseEntity<Evento> save(
            @Valid @RequestBody Evento evento,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long organizadorId
    ) {
        log.info("Solicitud de creación de evento: nombre='{}', genero='{}', organizadorId={}",
                evento.getNombre(), evento.getGenero(), organizadorId);
        if (organizadorId != null) {
            evento.setOrganizadorId(organizadorId);
        }
        Evento creado = eventoService.guardarEvento(evento);
        log.info("Evento creado exitosamente: id={}, nombre='{}'", creado.getId(), creado.getNombre());
        return ResponseEntity.ok(creado);
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
     * Listar eventos del organizador autenticado.
     */
    @GetMapping("/mis")
    public ResponseEntity<List<Evento>> listarMisEventos(
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {
        List<Evento> eventos = eventoService.listarPorOrganizador(usuarioId);
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }

    /**
     * Actualiza un evento existente, por Id.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Evento> updateEvento(@PathVariable Integer id, @Valid @RequestBody Evento evento) {
        evento.setId(id);
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
     * Método para que cuando se compre una entrada, se reste del stock. Este
     * método es consumido por RabbitMQ y por lógica.
     */
    @PutMapping("/actualizarStock/{id}/{cantidad}")
    public ResponseEntity<Void> actualizarStock(@PathVariable Integer id, @PathVariable Integer cantidad) {
        eventoService.actualizarStock(id, cantidad);
        return ResponseEntity.noContent().build();
    }

    /**
     * Método para restaurar stock cuando se libera una reserva.
     */
    @PutMapping("/restaurarStock/{id}/{cantidad}")
    public ResponseEntity<Void> restaurarStock(@PathVariable Integer id, @PathVariable Integer cantidad) {
        eventoService.restaurarStock(id, cantidad);
        return ResponseEntity.noContent().build();
    }

    /**
     * Revisa el stock de un evento.
     */
    @GetMapping("/stock/{check}")
    public String revisarStock() {
        return eventoService.revisarStock();
    }

    // GET para buscar por genero, nombre y ubicación.
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

    /** 
     * Cambia el estado del evento (publicado, cancelado). 
     * Cancelación sólo Organizador.
    */ 
    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam String estado) {
        eventoService.cambiarEstado(id, estado);
        return ResponseEntity.noContent().build();
    }
}
