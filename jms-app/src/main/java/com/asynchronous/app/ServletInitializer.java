package com.asynchronous.app;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    @NonNull
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JmsAppApplication.class);
    }

}
