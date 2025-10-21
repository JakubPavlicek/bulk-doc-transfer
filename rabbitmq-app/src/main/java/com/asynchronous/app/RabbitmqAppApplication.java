package com.asynchronous.app;

import com.asynchronous.app.config.RabbitMQProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RabbitMQProperties.class)
@SpringBootApplication(scanBasePackages = { "com.asynchronous", "com.shared" })
public class RabbitmqAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqAppApplication.class, args);
    }

}
