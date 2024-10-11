package com.y_lab.project;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {
    private String id;
    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password) {
        this.id = UUID.randomUUID().toString(); // Генерируем уникальный идентификатор
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
