package com.y_lab.project.repository;

import com.y_lab.project.entity.Habit;
import java.util.*;

public class HabitRepository {
    private final Map<String, List<Habit>> userHabits = new HashMap<>();

    public void addHabit(String userId, Habit habit) {
        userHabits.putIfAbsent(userId, new ArrayList<>());
        userHabits.get(userId).add(habit);
    }

    public Optional<Habit> findById(String userId, String habitId) {
        List<Habit> habits = userHabits.getOrDefault(userId, Collections.emptyList());
        return habits.stream()
                .filter(habit -> habit.getId().equals(habitId))
                .findFirst();
    }

    public boolean updateHabit(String userId, Habit updatedHabit) {
        List<Habit> habits = userHabits.get(userId);
        if (habits != null) {
            for (int i = 0; i < habits.size(); i++) {
                if (habits.get(i).getId().equals(updatedHabit.getId())) {
                    habits.set(i, updatedHabit);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteHabit(String userId, String habitId) {
        List<Habit> habits = userHabits.get(userId);
        if (habits != null) {
            return habits.removeIf(habit -> habit.getId().equals(habitId));
        }
        return false;
    }

    public List<Habit> findAllByUserId(String userId) {
        return new ArrayList<>(userHabits.getOrDefault(userId, Collections.emptyList()));
    }
}

