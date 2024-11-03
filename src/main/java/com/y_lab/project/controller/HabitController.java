package com.y_lab.project.controller;

import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.mapper.UserMapper;
import com.y_lab.project.repository.HabitRepositoryJdbcImpl;
import com.y_lab.project.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;
    private final HabitRepositoryJdbcImpl habitRepositoryJdbcImpl;
    private final UserMapper userMapper;


    @Autowired
    public HabitController(HabitService habitService, HabitRepositoryJdbcImpl habitRepositoryJdbcImpl, UserMapper userMapper) {
        this.habitService = habitService;
        this.habitRepositoryJdbcImpl = habitRepositoryJdbcImpl;
        this.userMapper = userMapper;
    }

    private UserDTO getUserFromSession(HttpSession session) {
        return userMapper.toUserDTO((User) session.getAttribute("user"));
    }

    @GetMapping
    @Operation(summary = "Получить все привычки пользователя")
    public ResponseEntity<List<Habit>> getAllHabits(HttpSession session) {
        UserDTO userDTO = getUserFromSession(session);
        List<Habit> habits = habitRepositoryJdbcImpl.findAllByUser(userDTO);
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить привычку по ID")
    public ResponseEntity<?> getHabitById(@PathVariable Long id, HttpSession session) {
        UserDTO userDTO = getUserFromSession(session);
        Optional<Habit> habit = habitRepositoryJdbcImpl.findByIdAndUser(id, userDTO);

        if (habit.isPresent()) {
            return ResponseEntity.ok(habit.get());
        } else {
            return ResponseEntity.status(404).body("{\"error\":\"Привычка не найдена\"}");
        }
    }


    @PutMapping("/{id}")
    @Operation(summary = "Обновить привычку по ID")
    public ResponseEntity<String> updateHabit(
            @PathVariable Long id,
            @RequestBody HabitDTO habitDTO,
            HttpSession session) {

        UserDTO userDTO = getUserFromSession(session);
        String result = habitService.updateHabit(userDTO, id, habitDTO);

        if ("Привычка успешно обновлена!".equals(result)) {
            return ResponseEntity.ok("{\"message\":\"" + result + "\"}");
        } else {
            return ResponseEntity.status(404).body("{\"error\":\"" + result + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить привычку по ID")
    public ResponseEntity<String> deleteHabit(@PathVariable Long id, HttpSession session) {
        UserDTO userDTO = getUserFromSession(session);
        Optional<Habit> habit = habitRepositoryJdbcImpl.findByIdAndUser(id, userDTO);

        if (habit.isPresent()) {
            habitService.deleteHabit(userDTO, id);
            return ResponseEntity.ok("{\"status\":\"Привычка успешно удалена\"}");
        } else {
            return ResponseEntity.status(404).body("{\"error\":\"Привычка не найдена\"}");
        }
    }
}

