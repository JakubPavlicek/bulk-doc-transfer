package com.synchronous.app;

import com.shared.core.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(GlobalExceptionHandler.class)
@SpringBootApplication(scanBasePackages = { "com.synchronous", "com.shared" })
public class SynchronousAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynchronousAppApplication.class, args);
    }

}
