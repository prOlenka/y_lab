package com.y_lab.project.service;

import com.y_lab.project.config.AdminProperties;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.User;
import com.y_lab.project.mapper.UserMapper;
import com.y_lab.project.repository.UserRepository;
import com.y_lab.project.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdminProperties adminProperties;

    public UserService(JwtUtil jwtUtil, UserRepository userRepository, UserMapper userMapper, AdminProperties adminProperties) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.adminProperties = adminProperties;

        if (userRepository.findByEmail(adminProperties.getEmail()).isEmpty()) {
            User adminUser = new User(
                    adminProperties.getEmail(),
                    adminProperties.getPassword(),
                    adminProperties.getName(),
                    adminProperties.isAdmin()
            );
            userRepository.save(adminUser);
        }
    }

    public UserDTO getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        return userMapper.toUserDTO(userRepository.findByUsername(username));
    }

    public boolean isAdmin(UserDTO userDTO) {
        return userDTO.isAdmin();
    }

    public List<UserDTO> getAllUsers(UserDTO adminDTO) {
        if (isAdmin(adminDTO)) {
            return userRepository.findAll().stream()
                    .map(userMapper::toUserDTO)
                    .collect(Collectors.toList());
        } else {
            throw new SecurityException("Доступ запрещен: у вас нет прав администратора.");
        }
    }

    public String registerUser(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            return "Пользователь с таким email уже существует";
        }

        User newUser = userMapper.toEntity(userDTO);
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
        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isEmpty()) {
            return "Пользователь не найден";
        }

        userRepository.delete(userOptional.get());
        return "Аккаунт удален";
    }

    public String changePassword(UserDTO userDTO, String oldPassword, String newPassword) {
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
