package com.y_lab.project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
}