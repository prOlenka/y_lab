package com.y_lab.project.repository;

import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;

import java.util.List;
import java.util.Optional;

public interface HabitRepository {

    List<Habit> findAllByUser(User user);

    Optional<Habit> findByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);

    void save(Habit habit);
}


