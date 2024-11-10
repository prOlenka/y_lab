package com.y_lab.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomSpringConfig {

    @Bean
    public CustomSpringListener customSpringListener() {
        return new CustomSpringListener();
    }

}
