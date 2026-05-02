package com.ticketti.ms_eventos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_EVENTOS = "eventos.cola";
    public static final String COLA_EXCHANGE = "eventos.exchange";
    public static final String ROUTING_KEY_EVENTOS = "eventos.exchange";

    @Bean
    public Queue colaEventos() {
        return new Queue(COLA_EVENTOS, true);
    }

    @Bean
    public TopicExchange exchangeEventos() {
        return new TopicExchange(COLA_EXCHANGE);
    }

    @Bean
    public Binding bindingEventos(Queue colaEventos, TopicExchange intermediario) {
        return BindingBuilder.bind(colaEventos).to(intermediario).with(ROUTING_KEY_EVENTOS);
    }
}
