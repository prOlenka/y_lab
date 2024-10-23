package com.y_lab.project.service;

import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HabitService {
    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public String addHabit(User user, String name, String description, String frequency) {
        Habit newHabit = new Habit(user, name, description, frequency);
        habitRepository.save(newHabit);
        return "Привычка успешно добавлена!";
    }

    public String updateHabit(User user, Long habitId, String newName, String newDescription, String newFrequency) {
        Optional<Habit> habitOptional = habitRepository.findByIdAndUser(habitId, user);
        if (habitOptional.isPresent()) {
            Habit habit = habitOptional.get();
            habit.setName(newName);
            habit.setDescription(newDescription);
            habit.setFrequency(newFrequency);
            habitRepository.save(habit);
            return "Привычка успешно обновлена!";
        } else {
            return "Привычка не найдена";
        }
    }

    public String deleteHabit(User user, Long habitId) {
        Optional<Habit> habitOptional = habitRepository.findByIdAndUser(habitId, user);
        if (habitOptional.isPresent()) {
            habitRepository.deleteByIdAndUser(habitId, user);
            return "Привычка успешно удалена!";
        } else {
            return "Привычка не найдена";
        }
    }

    public List<Habit> listUserHabits(User user) {
        return habitRepository.findAllByUser(user);
    }

    public String generateHabitStatistics(User user, Long habitId, String period) {
        Optional<Habit> habitOptional = habitRepository.findByIdAndUser(habitId, user);
        if (habitOptional.isPresent()) {
            Habit habit = habitOptional.get();
            return habit.getStatistics(period);
        } else {
            return "Привычка не найдена";
        }
    }

    public int calculateStreak(User user, Long habitId) {
        Optional<Habit> habitOptional = habitRepository.findByIdAndUser(habitId, user);

        if (habitOptional.isEmpty()) {
            return -1;
        }

        Habit habit = habitOptional.get();
        List<LocalDate> completionDates = habit.getCompletionDates();

        if (completionDates.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate today = LocalDate.now();

        for (int i = completionDates.size() - 1; i >= 0; i--) {
            LocalDate date = completionDates.get(i);

            if (date.equals(today.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    public Optional<Long> findHabitIdByName(User user, String habitName) {
        return habitRepository.findAllByUser(user).stream()
                .filter(habit -> habit.getName().equalsIgnoreCase(habitName))
                .map(Habit::getId)
                .findFirst();
    }
}
