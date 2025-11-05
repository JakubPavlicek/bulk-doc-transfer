package com.asynchronous.app;

import com.shared.core.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Import(GlobalExceptionHandler.class)
@SpringBootApplication(scanBasePackages = { "com.asynchronous", "com.shared" })
public class JmsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmsAppApplication.class, args);
    }

}
