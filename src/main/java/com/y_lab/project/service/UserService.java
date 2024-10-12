package com.y_lab.project.service;

import com.y_lab.project.entity.User;
import com.y_lab.project.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Автоматическое добавление пользователя admin при запуске
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            userRepository.save(new User(UUID.randomUUID().toString(), "admin@admin.com", "admin", "Administrator", true));
        }
    }
    public boolean isAdmin(String userId) {
        return userRepository.findById(userId)
                .map(User::isAdmin)
                .orElse(false);
    }

    public List<User> getAllUsers(String adminId) {
        if (isAdmin(adminId)) {
            return userRepository.findAll();
        } else {
            throw new SecurityException("Доступ запрещен: у вас нет прав администратора.");
        }
    }

    public String registerUser(String email, String password, String name) {
        // Проверка на уникальность email
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return "Пользователь с таким email уже существует";
        }

        User newUser = new User(UUID.randomUUID().toString(), name, email, password, false);
        userRepository.save(newUser);
        return "Регистрация прошла успешно";
    }


    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Пользователь с таким email не найден";
        }
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return "Неверный пароль";
        }
        return "Вход выполнен успешно";
    }

    public String updateProfile(String userId, String newName, String newEmail) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Пользователь не найден";
        }
        user.updateProfile(newName, newEmail);
        userRepository.save(user);
        return "Профиль обновлен";
    }

    public String deleteUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Пользователь не найден";
        }
        userRepository.deleteById(userId);
        return "Аккаунт удален";
    }

    public String changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Пользователь не найден";
        }
        if (!user.getPassword().equals(oldPassword)) {
            return "Старый пароль неверен";
        }
        user.changePassword(newPassword);
        userRepository.save(user);
        return "Пароль изменен";
    }
}

