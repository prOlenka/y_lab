package com.y_lab.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingAutoConfiguration {

    @Bean
    public ExecutionTimeLoggerConfig executionTimeLoggerConfig() {
        return new ExecutionTimeLoggerConfig();
    }
}
