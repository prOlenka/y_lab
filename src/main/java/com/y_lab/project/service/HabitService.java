package com.y_lab.project.service;

import com.y_lab.project.entity.Habit;
import com.y_lab.project.repository.HabitRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HabitService {
    private HabitRepository habitRepository;
    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public String addHabit(String userId, String name, String description, String frequency) {
        String id = UUID.randomUUID().toString();
        Habit newHabit = new Habit(userId, name, description, frequency);
        habitRepository.addHabit(userId, newHabit);
        return "Привычка успешно добавлена!";
    }

    public String updateHabit(String userId, String habitId, String newName, String newDescription, String newFrequency) {
        Optional<Habit> habitOptional = habitRepository.findById(userId, habitId);
        if (habitOptional.isPresent()) {
            Habit habit = habitOptional.get();
            habit.setName(newName);
            habit.setDescription(newDescription);
            habit.setFrequency(newFrequency);
            boolean isUpdated = habitRepository.updateHabit(userId, habit);
            return isUpdated ? "Привычка успешно обновлена!" : "Ошибка при обновлении привычки";
        } else {
            return "Привычка не найдена";
        }
    }

    public String deleteHabit(String userId, String habitId) {
        boolean isDeleted = habitRepository.deleteHabit(userId, habitId);
        return isDeleted ? "Привычка успешно удалена!" : "Привычка не найдена";
    }

    public List<Habit> listUserHabits(String userId) {
        return habitRepository.findAllByUserId(userId);
    }

    public String generateHabitStatistics(String userId, String habitId, String period) {
        Optional<Habit> habitOptional = habitRepository.findById(userId, habitId);
        if (habitOptional.isPresent()) {
            Habit habit = habitOptional.get();
            return habit.getStatistics(period);
        } else {
            return "Привычка не найдена";
        }
    }

    public int calculateStreak(String userId, String habitId) {
        Optional<Habit> habitOptional = habitRepository.findById(userId, habitId);

        if (habitOptional.isEmpty()) {
            return -1; // Привычка не найдена
        }

        Habit habit = habitOptional.get();
        List<LocalDate> completionDates = habit.getCompletionDates(); // Предполагается, что метод возвращает отсортированный список дат выполнения

        if (completionDates.isEmpty()) {
            return 0; // Нет выполнений, серия равна нулю
        }

        int streak = 0;
        LocalDate today = LocalDate.now();

        // Идем с конца списка выполнений и проверяем последовательность дат
        for (int i = completionDates.size() - 1; i >= 0; i--) {
            LocalDate date = completionDates.get(i);

            // Проверяем, является ли дата выполнения сегодня или в предыдущие дни без пропусков
            if (date.equals(today.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    public Optional<String> findHabitIdByName(String userId, String habitName) {
        return habitRepository.findAllByUserId(userId).stream()
                .filter(habit -> habit.getName().equalsIgnoreCase(habitName))
                .map(Habit::getId)
                .findFirst();
    }


}
