package com.y_lab.project.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
public class Habit {
    private String id;
    private String userId;
    private String name;
    private String description;
    private String frequency; // "daily" или "weekly"
    private List<LocalDate> completionDates = new ArrayList<>();


    public Habit(String userId, String name, String description, String frequency) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
    }


    public String getStatistics(String period) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();

        List<LocalDate> filteredDates = completionDates.stream()
                .filter(date -> isWithinPeriod(date, now, period))
                .toList();

        int totalDays = getTotalDaysForPeriod(period);
        int completedDays = filteredDates.size();

        // Рассчитываем процент выполненных дней за указанный период
        double completionPercentage = totalDays > 0 ? (double) completedDays / totalDays * 100 : 0.0;

        return String.format("Статистика за период %s: Выполнено %d из %d дней (%.2f%%)",
                period, completedDays, totalDays, completionPercentage);
    }

    // Метод для проверки, находится ли дата в указанном периоде (день, неделя, месяц)
    private boolean isWithinPeriod(LocalDate date, LocalDate now, String period) {
        switch (period.toLowerCase()) {
            case "день":
                return date.equals(now);
            case "неделя":
                return date.isAfter(now.minusWeeks(1)) && !date.isAfter(now);
            case "месяц":
                return date.isAfter(now.minusMonths(1)) && !date.isAfter(now);
            default:
                throw new IllegalArgumentException("Неверный период: " + period);
        }
    }

    // Метод для получения общего количества дней в зависимости от периода
    private int getTotalDaysForPeriod(String period) {
        switch (period.toLowerCase()) {
            case "день":
                return 1;
            case "неделя":
                return 7;
            case "месяц":
                return LocalDate.now().lengthOfMonth();
            case "все время":
                return completionDates.isEmpty() ? 0 : (int) completionDates.stream()
                        .distinct()
                        .count(); // Можно подсчитать уникальные дни выполнения
            default:
                throw new IllegalArgumentException("Неверный период: " + period);
        }
    }
}
