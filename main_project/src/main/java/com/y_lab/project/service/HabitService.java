package com.y_lab.project.service;

import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.mapper.HabitMapper;
import com.y_lab.project.repository.HabitRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;

    public HabitService(HabitRepository habitRepository, HabitMapper habitMapper) {
        this.habitRepository = habitRepository;
        this.habitMapper = habitMapper;
    }

    public String addHabit(@Valid HabitDTO habitDTO) {
        Habit newHabit = habitMapper.toEntity(habitDTO);
        habitRepository.save(newHabit);
        return "Привычка успешно добавлена!";
    }

    public String updateHabit(UserDTO user, Long habitId, @Valid HabitDTO habitDTO) {
        return habitRepository.findByIdAndUser(habitId, user)
                .map(habit -> {
                    habit.setName(habitDTO.getName());
                    habit.setDescription(habitDTO.getDescription());
                    habit.setFrequency(habitDTO.getFrequency());
                    habitRepository.save(habit);
                    return "Привычка успешно обновлена!";
                })
                .orElse("Привычка не найдена");
    }

    public String deleteHabit(UserDTO user, Long habitId) {
        return habitRepository.findByIdAndUser(habitId, user)
                .map(habit -> {
                    habitRepository.deleteByIdAndUser(habitId, user);
                    return "Привычка успешно удалена!";
                })
                .orElse("Привычка не найдена");
    }

    public List<HabitDTO> listUserHabits(UserDTO user) {
        return habitRepository.findAllByUser(user).stream()
                .map(habitMapper::toHabitDTO)
                .collect(Collectors.toList());
    }

    public String generateHabitStatistics(UserDTO user, Long habitId, String period) {
        return habitRepository.findByIdAndUser(habitId, user)
                .map(habit -> getStatistics(user, habit, period))
                .orElse("Привычка не найдена");
    }

    private String getStatistics(UserDTO userDTO, Habit habit, String period) {
        LocalDate now = LocalDate.now();
        List<LocalDate> filteredDates = habit.getCompletionDates().stream()
                .filter(date -> isWithinPeriod(date, now, period))
                .toList();

        int totalDays = getTotalDaysForPeriod(userDTO, period);
        int completedDays = filteredDates.size();
        double completionPercentage = (totalDays > 0) ? (double) completedDays / totalDays * 100 : 0.0;

        return String.format("Статистика за период %s: Выполнено %d из %d дней (%.2f%%)",
                period, completedDays, totalDays, completionPercentage);
    }

    public int calculateStreak(UserDTO user, Long habitId) {
        return habitRepository.findByIdAndUser(habitId, user)
                .map(habit -> {
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
                })
                .orElse(-1);
    }

    public Optional<Long> findHabitIdByName(UserDTO user, String habitName) {
        return habitRepository.findAllByUser(user).stream()
                .filter(habit -> habit.getName().equalsIgnoreCase(habitName))
                .map(Habit::getId)
                .findFirst();
    }

    private boolean isWithinPeriod(LocalDate date, LocalDate now, String period) {
        return switch (period.toLowerCase()) {
            case "день" -> date.equals(now);
            case "неделя" -> date.isAfter(now.minusWeeks(1)) && !date.isAfter(now);
            case "месяц" -> date.isAfter(now.minusMonths(1)) && !date.isAfter(now);
            default -> throw new IllegalArgumentException("Неверный период: " + period);
        };
    }

    private int getTotalDaysForPeriod(UserDTO user, String period) {
        return switch (period.toLowerCase()) {
            case "день" -> 1;
            case "неделя" -> 7;
            case "месяц" -> LocalDate.now().lengthOfMonth();
            case "все время" -> habitRepository.findAllByUser(user).size();
            default -> throw new IllegalArgumentException("Неверный период: " + period);
        };
    }
}
