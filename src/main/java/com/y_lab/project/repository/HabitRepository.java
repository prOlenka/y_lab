package com.y_lab.project.repository;

import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    // Метод для поиска всех привычек пользователя по ID пользователя
    List<Habit> findAllByUser(User user);

    // Метод для поиска привычки по ID пользователя и ID привычки
    Optional<Habit> findByIdAndUser(Long id, User user);

    // Метод для удаления привычки по ID пользователя и ID привычки
    void deleteByIdAndUser(Long id, User user);
}

