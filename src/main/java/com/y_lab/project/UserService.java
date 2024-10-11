package com.y_lab.project;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String registerUser(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            return "Пользователь с таким email уже существует.";
        }
        User user = new User(name, email, password);
        userRepository.save(user);
        return "Регистрация прошла успешно.";
    }

    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Пользователь с таким email не найден.";
        }
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return "Неверный пароль.";
        }
        return "Вход выполнен успешно.";
    }
}

