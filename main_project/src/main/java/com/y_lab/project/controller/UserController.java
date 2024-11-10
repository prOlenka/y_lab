package com.y_lab.project.controller;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        String result = userService.registerUser(userDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    @Operation(summary = "User login and token generation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, token generated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        return userService.loginUser(email, password)
                .map(token -> ResponseEntity.ok("Bearer " + token))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }

    @PutMapping("/updateProfile")
    @Operation(summary = "Update user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<String> updateProfile(@RequestBody UserDTO userDTO,
                                                @RequestParam String newName,
                                                @RequestParam String newEmail) {
        String result = userService.updateProfile(userDTO, newName, newEmail);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<String> deleteUser(@RequestBody UserDTO userDTO) {
        String result = userService.deleteUser(userDTO);
        return ResponseEntity.ok(result);
    }
}
