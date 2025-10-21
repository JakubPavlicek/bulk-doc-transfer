package com.asynchronous.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.asynchronous", "com.shared" })
public class JmsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmsAppApplication.class, args);
    }

}
