package com.y_lab.project;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {
    private Map<String, User> users = new HashMap<>();

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public void delete(User user) {
        users.remove(user.getId());
    }
}
