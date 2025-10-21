package com.asynchronous.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    @Bean
    public Queue queue() {
        return new Queue(properties.getQueueName());
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(properties.getExchangeName());
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                             .to(exchange)
                             .with(properties.getRoutingKey());
    }

}
