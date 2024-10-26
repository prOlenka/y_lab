package com.y_lab.project.service;

import com.y_lab.project.ConfigLoader;
import com.y_lab.project.ValidationUtil;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.User;
import com.y_lab.project.mapper.UserMapper;
import com.y_lab.project.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        ConfigLoader configLoader = new ConfigLoader("config.properties");

        String adminEmail = configLoader.getProperty("admin.email");
        String adminPassword = configLoader.getProperty("admin.password");
        String adminName = configLoader.getProperty("admin.name");
        boolean isAdmin = configLoader.getBooleanProperty("admin.isAdmin");

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User adminUser = new User(adminEmail, adminPassword, adminName, isAdmin);
            userRepository.save(adminUser);
        }
    }

    public boolean isAdmin(UserDTO userDTO) {
        ValidationUtil.validate(userDTO);
        return userDTO.isAdmin();
    }

    public List<UserDTO> getAllUsers(UserDTO adminDTO) {
        ValidationUtil.validate(adminDTO);
        if (isAdmin(adminDTO)) {
            return userRepository.findAll().stream()
                    .map(userMapper::toUserDTO)
                    .collect(Collectors.toList());
        } else {
            throw new SecurityException("Доступ запрещен: у вас нет прав администратора.");
        }
    }

    public String registerUser(UserDTO userDTO) {
        ValidationUtil.validate(userDTO);
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            return "Пользователь с таким email уже существует";
        }

        User newUser = userMapper.toUser(userDTO);
        userRepository.save(newUser);
        return "Регистрация прошла успешно";
    }

    public Optional<UserDTO> loginUser(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email и пароль не могут быть пустыми");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(userMapper.toUserDTO(user));
            }
        }
        return Optional.empty();
    }

    public String updateProfile(UserDTO userDTO, String newName, String newEmail) {
        ValidationUtil.validate(userDTO);
        if (newName == null || newName.isBlank() || newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Имя и email не могут быть пустыми");
        }

        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isEmpty()) {
            return "Пользователь не найден";
        }

        User user = userOptional.get();
        user.setName(newName);
        user.setEmail(newEmail);
        userRepository.save(user);
        return "Профиль обновлен";
    }

    public String deleteUser(UserDTO userDTO) {
        ValidationUtil.validate(userDTO);

        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isEmpty()) {
            return "Пользователь не найден";
        }

        userRepository.delete(userOptional.get());
        return "Аккаунт удален";
    }

    public String changePassword(UserDTO userDTO, String oldPassword, String newPassword) {
        ValidationUtil.validate(userDTO);
        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Пароли не могут быть пустыми");
        }

        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isEmpty()) {
            return "Пользователь не найден";
        }

        User user = userOptional.get();
        if (!user.getPassword().equals(oldPassword)) {
            return "Старый пароль неверен";
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        return "Пароль изменен";
    }
}
