package com.y_lab.project.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.ToString;

@Entity
@Table(name = "users", schema = "app_schema")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "app_schema.user_seq", allocationSize = 1)

    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    public User(String email, String password, String name, boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
    }
}
