package com.y_lab.project.controller;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Зарегистрировать пользователя")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        String result = userService.registerUser(userDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя")
    public ResponseEntity<UserDTO> loginUser(@RequestParam String email, @RequestParam String password) {
        return userService.loginUser(email, password)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).body(null));
    }

    @PutMapping("/updateProfile")
    @Operation(summary = "Обновить профиль")
    public ResponseEntity<String> updateProfile(@RequestBody UserDTO userDTO,
                                                @RequestParam String newName,
                                                @RequestParam String newEmail) {
        String result = userService.updateProfile(userDTO, newName, newEmail);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удалить пользователя")
    public ResponseEntity<String> deleteUser(@RequestBody UserDTO userDTO) {
        String result = userService.deleteUser(userDTO);
        return ResponseEntity.ok(result);
    }
}

