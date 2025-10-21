package com.asynchronous.app.service;

import com.asynchronous.app.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQProducer {

    private final RabbitMQProperties rabbitMQProperties;
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        log.info("Sending message: {}", message);

        rabbitTemplate.convertAndSend(
            rabbitMQProperties.getExchangeName(),
            rabbitMQProperties.getRoutingKey(),
            message
        );
    }

}
