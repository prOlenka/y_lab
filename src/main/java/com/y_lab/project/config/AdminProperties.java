package com.y_lab.project.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }
}
