package com.ticketti.ms_eventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketti.ms_eventos.model.Estado;
import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.model.Genero;
import com.ticketti.ms_eventos.service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventoControllerTest {

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoController eventoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Monta el MockMvc manualmente, sin levantar contexto de Spring
        mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
    }

    private Evento crearEventoValido() {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setNombre("Lollapalooza");
        evento.setDescripcion("Festival de música");
        evento.setGenero(Genero.ROCK);
        evento.setEstado(Estado.PUBLICADO);
        evento.setAforo(100);
        evento.setStock(50);
        evento.setPrecioEntrada(15000.0);
        return evento;
    }

    @Test
    void crear_eventoValido_retorna200() throws Exception {
        Evento evento = crearEventoValido();
        when(eventoService.guardarEvento(any(Evento.class))).thenReturn(evento);

        mockMvc.perform(post("/api/v0/Eventos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Usuario-Id", "5")
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Lollapalooza"));
    }

    @Test
    void listarEventos_conResultados_retorna200() throws Exception {
        when(eventoService.listarEventos()).thenReturn(List.of(crearEventoValido()));

        mockMvc.perform(get("/api/v0/Eventos/listarEventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lollapalooza"));
    }

    @Test
    void listarEventos_vacio_retorna204() throws Exception {
        when(eventoService.listarEventos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v0/Eventos/listarEventos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listarMisEventos_conHeader_retorna200() throws Exception {
        when(eventoService.listarPorOrganizador(5L)).thenReturn(List.of(crearEventoValido()));

        mockMvc.perform(get("/api/v0/Eventos/mis").header("X-Usuario-Id", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lollapalooza"));
    }

    @Test
    void buscarEvento_existente_retorna200() throws Exception {
        when(eventoService.buscarPorId(1)).thenReturn(Optional.of(crearEventoValido()));

        mockMvc.perform(get("/api/v0/Eventos/buscarEvento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscarEvento_noExistente_retorna404() throws Exception {
        when(eventoService.buscarPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v0/Eventos/buscarEvento/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarEvento_retorna204() throws Exception {
        doNothing().when(eventoService).eliminarEvento(1);

        mockMvc.perform(delete("/api/v0/Eventos/eliminarEvento/1"))
                .andExpect(status().isNoContent());

        verify(eventoService, times(1)).eliminarEvento(1);
    }

    @Test
    void actualizarStock_retorna204() throws Exception {
        doNothing().when(eventoService).actualizarStock(1, 5);

        mockMvc.perform(put("/api/v0/Eventos/actualizarStock/1/5"))
                .andExpect(status().isNoContent());

        verify(eventoService, times(1)).actualizarStock(1, 5);
    }

    @Test
    void buscar_conFiltros_retorna200() throws Exception {
        when(eventoService.buscarEventos(Genero.ROCK, "Lolla", null))
                .thenReturn(List.of(crearEventoValido()));

        mockMvc.perform(get("/api/v0/Eventos/buscar")
                        .param("genero", "ROCK")
                        .param("nombre", "Lolla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lollapalooza"));
    }

    @Test
    void cambiarEstado_retorna204() throws Exception {
        doNothing().when(eventoService).cambiarEstado(1, "CANCELADO");

        mockMvc.perform(put("/api/v0/Eventos/1/estado").param("estado", "CANCELADO"))
                .andExpect(status().isNoContent());

        verify(eventoService, times(1)).cambiarEstado(1, "CANCELADO");
    }
}