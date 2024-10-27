package com.y_lab.project.repository;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;

import java.util.List;
import java.util.Optional;

public interface HabitRepository {

    List<Habit> findAllByUser(UserDTO user);

    Optional<Habit> findByIdAndUser(Long id, UserDTO user);

    void deleteByIdAndUser(Long id, UserDTO user);

    void save(Habit habit);
}


