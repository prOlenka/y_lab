package com.y_lab.project.repository;

import com.y_lab.project.entity.User;

import java.util.*;

public class UserRepository {
    private final Map<String, User> users = new LinkedHashMap<>();

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public void deleteById(String id) {
        users.remove(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
