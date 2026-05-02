package com.ticketti.ms_eventos.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.ticketti.ms_eventos.config.RabbitMQConfig;
import com.ticketti.ms_eventos.model.Evento;

@Service
public class EntradaConsumer {

    @RabbitListener(queues = RabbitMQConfig.COLA_EVENTOS)
    public void recibirEntrada(Evento evento) {
        System.out.println("Mensaje recibido de RabbitMQ");
        System.out.println("Evento        : " + evento.getNombre());
        System.out.println("Género        : " + evento.getGenero());
        System.out.println("Stock         : " + evento.getStock());
        System.out.println("Precio entrada: " + evento.getPrecioEntrada());
    }
}