package com.y_lab.project.controller;

import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.mapper.UserMapper;
import com.y_lab.project.repository.HabitRepository;
import com.y_lab.project.repository.HabitRepositoryJdbcImpl;
import com.y_lab.project.service.HabitService;
import com.y_lab.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;
    private final UserService userService;
    private final HabitRepository habitRepository;
    private final UserMapper userMapper;


    @GetMapping
    @Operation(summary = "Получить все привычки пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно возвращен список привычек"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный запрос"),
            @ApiResponse(responseCode = "403", description = "Запрещено")
    })
    public ResponseEntity<List<Habit>> getAllHabits(@RequestHeader("Authorization") String token) {
        UserDTO userDTO = userService.getUserFromToken(token);
        List<Habit> habits = habitRepository.findAllByUser(userDTO);
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить привычку по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Привычка найдена"),
            @ApiResponse(responseCode = "404", description = "Привычка не найдена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный запрос")
    })
    public ResponseEntity<?> getHabitById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        UserDTO userDTO = userService.getUserFromToken(token);
        Optional<Habit> habit = habitRepository.findByIdAndUser(id, userDTO);

        return habit.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Привычка не найдена\"}"));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Обновить привычку по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Привычка успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Привычка не найдена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный запрос")
    })
    public ResponseEntity<String> updateHabit(
            @PathVariable Long id,
            @RequestBody HabitDTO habitDTO,
            @RequestHeader("Authorization") String token) {

        UserDTO userDTO = userService.getUserFromToken(token);
        String result = habitService.updateHabit(userDTO, id, habitDTO);

        if ("Привычка успешно обновлена!".equals(result)) {
            return ResponseEntity.ok("{\"message\":\"" + result + "\"}");
        } else {
            return ResponseEntity.status(404).body("{\"error\":\"" + result + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить привычку по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Привычка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Привычка не найдена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный запрос")
    })
    public ResponseEntity<String> deleteHabit(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        UserDTO userDTO = userService.getUserFromToken(token);
        Optional<Habit> habit = habitRepository.findByIdAndUser(id, userDTO);
        if (habit.isPresent()) {
            habitService.deleteHabit(userDTO, id);
            return ResponseEntity.ok("{\"status\":\"Привычка успешно удалена\"}");
        } else {
            return ResponseEntity.status(404).body("{\"error\":\"Привычка не найдена\"}");
        }
    }
}

