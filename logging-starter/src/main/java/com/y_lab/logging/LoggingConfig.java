package com.y_lab.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {
    @Bean
    public CustomLogger customLogger() {
        return new CustomLogger();
    }
}

