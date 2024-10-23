package com.y_lab.project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "habits", schema = "app_schema")
@Getter
@Setter
@NoArgsConstructor
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habit_seq")
    @SequenceGenerator(name = "habit_seq", sequenceName = "app_schema.habit_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String frequency;

    @ElementCollection
    @CollectionTable(
            name = "habit_completion_dates",
            schema = "app_schema",
            joinColumns = @JoinColumn(name = "habit_id")
    )
    @Column(name = "completion_date")
    private List<LocalDate> completionDates = new ArrayList<>();



    public Habit(User user, String name, String description, String frequency) {
        this.user = user;
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
