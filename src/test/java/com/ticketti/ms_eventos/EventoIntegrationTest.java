package com.ticketti.ms_eventos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketti.ms_eventos.model.Estado;
import com.ticketti.ms_eventos.model.Evento;
import com.ticketti.ms_eventos.model.Genero;
import com.ticketti.ms_eventos.model.Recinto;
import com.ticketti.ms_eventos.repository.EventoRepository;
import com.ticketti.ms_eventos.service.EntradaProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de integración: levanta el contexto completo de Spring
 * (Controller → Service → Repository) contra una base de datos H2 real.
 * MockMvc se configura manualmente con webAppContextSetup, evitando
 * el bug de empaquetado de @AutoConfigureMockMvc en Spring Boot 4.0.x.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
@ActiveProfiles("test")
class EventoIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EntradaProducer entradaProducer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        eventoRepository.deleteAll();
    }

    @AfterEach
    void limpiarDespuesDeCadaTest() {
        eventoRepository.deleteAll();
    }

    private Evento crearEventoDePrueba() {
        Evento evento = new Evento();
        evento.setNombre("Lollapalooza");
        evento.setDescripcion("Festival de música");
        evento.setFecha(new java.util.Date());
        evento.setGenero(Genero.ROCK);
        evento.setEstado(Estado.PUBLICADO);
        evento.setAforo(100);
        evento.setStock(50);
        evento.setPrecioEntrada(15000.0);

        Recinto recinto = new Recinto();
        recinto.setNombre("Parque O'Higgins");
        recinto.setUbicacion("Santiago Centro");
        evento.setRecinto(recinto);

        return evento;
    }

    @Test
    void crearEvento_sePersisteEnBaseDeDatosReal() throws Exception {
        Evento evento = crearEventoDePrueba();

        mockMvc.perform(post("/api/v0/Eventos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Usuario-Id", "5")
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Lollapalooza"));

        assertEquals(1, eventoRepository.findAll().size());
        assertEquals("Lollapalooza", eventoRepository.findAll().get(0).getNombre());
    }

    @Test
    void crearEvento_stockMayorQueAforo_noSePersisteYRetorna400() throws Exception {
        Evento evento = crearEventoDePrueba();
        evento.setStock(200);

        mockMvc.perform(post("/api/v0/Eventos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isBadRequest());

        assertTrue(eventoRepository.findAll().isEmpty());
    }

    @Test
    void listarEventos_devuelveLosEventosGuardadosRealmente() throws Exception {
        eventoRepository.save(crearEventoDePrueba());

        mockMvc.perform(get("/api/v0/Eventos/listarEventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lollapalooza"));
    }

    @Test
    void actualizarStock_descuentaEnBaseDeDatosReal() throws Exception {
        Evento guardado = eventoRepository.save(crearEventoDePrueba());

        mockMvc.perform(put("/api/v0/Eventos/actualizarStock/" + guardado.getId() + "/10"))
                .andExpect(status().isNoContent());

        Evento actualizado = eventoRepository.findById(guardado.getId()).orElseThrow();
        assertEquals(40, actualizado.getStock());
    }

    @Test
    void buscarEventos_filtraPorGeneroCorrectamenteEnBDReal() throws Exception {
        eventoRepository.save(crearEventoDePrueba());

        Evento otroEvento = crearEventoDePrueba();
        otroEvento.setNombre("Festival Indie");
        otroEvento.setGenero(Genero.INDIE);
        eventoRepository.save(otroEvento);

        mockMvc.perform(get("/api/v0/Eventos/buscar").param("genero", "ROCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].genero").value("ROCK"));
    }
}