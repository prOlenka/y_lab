package com.y_lab.project.service;

import com.y_lab.project.entity.User;
import com.y_lab.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Автоматическое добавление пользователя admin при запуске
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            userRepository.save(new User("admin@admin.com", "admin", "Administrator", true));
        }
    }

    public boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }

    public List<User> getAllUsers(User admin) {
        if (isAdmin(admin)) {
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

        User newUser = new User(name, email, password, false);
        userRepository.save(newUser);
        return "Регистрация прошла успешно";
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public String updateProfile(User user, String newName, String newEmail) {
        if (user == null) {
            return "Пользователь не найден";
        }
        user.updateProfile(newName, newEmail);
        userRepository.save(user);
        return "Профиль обновлен";
    }

    public String deleteUser(User user) {
        if (user == null) {
            return "Пользователь не найден";
        }
        userRepository.delete(user);
        return "Аккаунт удален";
    }

    public String changePassword(User user, String oldPassword, String newPassword) {
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
