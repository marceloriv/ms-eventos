package com.ticketti.ms_eventos.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ticketti.ms_eventos.config.RabbitMQConfig;
import com.ticketti.ms_eventos.model.Evento;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EntradaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarEntradaComprada(Evento evento) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.COLA_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_EVENTOS,
                evento
            );
            System.out.println("Mensaje enviado a RabbitMQ");
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje a RabbitMQ (RabbitMQ podría estar apagado): " + e.getMessage());
        }
    }
}