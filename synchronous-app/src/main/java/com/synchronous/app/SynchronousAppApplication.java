package com.synchronous.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.synchronous", "com.shared" })
public class SynchronousAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynchronousAppApplication.class, args);
    }

}
