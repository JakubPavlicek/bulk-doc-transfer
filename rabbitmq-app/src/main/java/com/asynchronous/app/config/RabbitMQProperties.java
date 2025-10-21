package com.asynchronous.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    @Value("${rabbitmq.queue.name:queue}")
    private String queueName;

    @Value("${rabbitmq.exchange.name:exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:routing.key}")
    private String routingKey;

}
