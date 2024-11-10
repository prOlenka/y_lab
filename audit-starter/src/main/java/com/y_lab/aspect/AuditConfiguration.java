package com.y_lab.aspect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfiguration {

    @Bean
    public UserActionAuditAspect userActionAuditAspect() {
        return new UserActionAuditAspect();
    }
}

