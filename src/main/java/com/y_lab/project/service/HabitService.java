package com.y_lab.project.service;

import com.y_lab.project.ValidationUtil;
import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.mapper.HabitMapper;
import com.y_lab.project.repository.HabitRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HabitService {
    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;

    public HabitService(HabitRepository habitRepository, HabitMapper habitMapper) {
        this.habitRepository = habitRepository;
        this.habitMapper = habitMapper;
    }

    public String addHabit(HabitDTO habitDTO) {
        ValidationUtil.validate(habitDTO);
        Habit newHabit = habitMapper.toEntity(habitDTO);
        habitRepository.save(newHabit);
        return "Привычка успешно добавлена!";
    }

    public String updateHabit(UserDTO user, Long habitId, HabitDTO habitDTO) {
        ValidationUtil.validate(habitDTO);
        Optional<Habit> habitOptional = habitRepository.findByIdAndUser(habitId, user);
        if (habitOptional.isPresent()) {
            Habit habit = habitOptional.get();
            habit.setName(habitDTO.getName());
            habit.setDescription(habitDTO.getDescription());
            habit.setFrequency(habitDTO.getFrequency());
            habitRepository.save(habit);
            return "Привычка успешно обновлена!";
        } else {
            return "Привычка не найдена";
        }
    }

    public String deleteHabit(UserDTO user, Long habitId) {
        Optional<Habit> habitOptional = habitRepository.findByIdAndUser(habitId, user);
        if (habitOptional.isPresent()) {
            habitRepository.deleteByIdAndUser(habitId, user);
            return "Привычка успешно удалена!";
        } else {
            return "Привычка не найдена";
        }
    }

    public List<HabitDTO> listUserHabits(UserDTO user) {
        return habitRepository.findAllByUser(user).stream()
                .map(habitMapper::toHabitDTO)
                .collect(Collectors.toList());
    }

    public String generateHabitStatistics(UserDTO user, Long habitId, String period) {
        Optional<Habit> habitOptional = habitRepository.findByIdAndUser(habitId, user);
        if (habitOptional.isPresent()) {
            Habit habit = habitOptional.get();
            return getStatistics(habit, period);
        } else {
            return "Привычка не найдена";
        }
    }

    private String getStatistics(Habit habit, String period) {
        LocalDate now = LocalDate.now();
        List<LocalDate> filteredDates = habit.getCompletionDates().stream()
                .filter(date -> isWithinPeriod(date, now, period))
                .toList();

        int totalDays = getTotalDaysForPeriod(period);
        int completedDays = filteredDates.size();

        double completionPercentage = totalDays > 0 ? (double) completedDays / totalDays * 100 : 0.0;

        return String.format("Статистика за период %s: Выполнено %d из %d дней (%.2f%%)",
                period, completedDays, totalDays, completionPercentage);
    }

    public int calculateStreak(UserDTO user, Long habitId) {
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

    public Optional<Long> findHabitIdByName(UserDTO user, String habitName) {
        return habitRepository.findAllByUser(user).stream()
                .filter(habit -> habit.getName().equalsIgnoreCase(habitName))
                .map(Habit::getId)
                .findFirst();
    }

    private boolean isWithinPeriod(LocalDate date, LocalDate now, String period) {
        switch (period.toLowerCase()) {
            case "день" -> {
                return date.equals(now);
            }
            case "неделя" -> {
                return date.isAfter(now.minusWeeks(1)) && !date.isAfter(now);
            }
            case "месяц" -> {
                return date.isAfter(now.minusMonths(1)) && !date.isAfter(now);
            }
            default -> throw new IllegalArgumentException("Неверный период: " + period);
        }
    }

    private int getTotalDaysForPeriod(String period) {
        Habit habit = new Habit();
        return switch (period.toLowerCase()) {
            case "день" -> 1;
            case "неделя" -> 7;
            case "месяц" -> LocalDate.now().lengthOfMonth();
            case "все время" -> habit.getCompletionDates().isEmpty() ? 0 : (int) habit.getCompletionDates().stream()
                    .distinct()
                    .count();
            default -> throw new IllegalArgumentException("Неверный период: " + period);
        };
    }
}
