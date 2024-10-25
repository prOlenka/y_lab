package com.y_lab.project.service;

import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;

public class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private HabitService habitService;

    private User user;
    private Habit habit;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId("user123"); // Предположим, что у вашего пользователя есть метод setId или аналогичный
        habit = new Habit(user, "Exercise", "Daily morning exercise", "ежедневно");
    }

    @Test
    public void testAddHabit_Success() {
        String name = "Exercise";
        String description = "Daily morning exercise";
        String frequency = "ежедневно";

        String result = habitService.addHabit(user, name, description, frequency);

        assertEquals("Привычка успешно добавлена!", result);
        verify(habitRepository, times(1)).save(any(Habit.class)); // Проверяем, что save был вызван
    }

    @Test
    public void testUpdateHabit_Success() {
        String newName = "Reading";
        String newDescription = "Read books every evening";
        String newFrequency = "еженедельно";

        when(habitRepository.findByIdAndUser(habit.getId(), user)).thenReturn(Optional.of(habit));

        String result = habitService.updateHabit(user, habit.getId(), newName, newDescription, newFrequency);

        assertEquals("Привычка успешно обновлена!", result);
        assertEquals(newName, habit.getName());
        assertEquals(newDescription, habit.getDescription());
        assertEquals(newFrequency, habit.getFrequency());
        verify(habitRepository, times(1)).save(habit); // Проверяем, что save был вызван
    }

    @Test
    public void testUpdateHabit_HabitNotFound() {
        when(habitRepository.findByIdAndUser(habit.getId(), user)).thenReturn(Optional.empty());

        String result = habitService.updateHabit(user, habit.getId(), "New Name", "New Description", "еженедельно");

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, never()).save(any(Habit.class)); // Проверяем, что save не был вызван
    }

    @Test
    public void testDeleteHabit_Success() {
        when(habitRepository.findByIdAndUser(habit.getId(), user)).thenReturn(Optional.of(habit));

        String result = habitService.deleteHabit(user, habit.getId());

        assertEquals("Привычка успешно удалена!", result);
        verify(habitRepository, times(1)).deleteByIdAndUser(habit.getId(), user); // Проверяем, что deleteByIdAndUser был вызван
    }

    @Test
    public void testDeleteHabit_HabitNotFound() {
        when(habitRepository.findByIdAndUser(habit.getId(), user)).thenReturn(Optional.empty());

        String result = habitService.deleteHabit(user, habit.getId());

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, never()).deleteByIdAndUser(Long.valueOf(anyString()), any(User.class)); // Проверяем, что deleteByIdAndUser не был вызван
    }

    @Test
    public void testListUserHabits() {
        List<Habit> habits = new ArrayList<>();
        habits.add(new Habit(user, "Exercise", "Morning run", "ежедневно"));
        habits.add(new Habit(user, "Meditation", "10 minutes of meditation", "ежедневно"));

        when(habitRepository.findAllByUser(user)).thenReturn(habits);

        List<Habit> result = habitService.listUserHabits(user);

        assertEquals(2, result.size());
        assertEquals("Exercise", result.get(0).getName());
        assertEquals("Meditation", result.get(1).getName());
        verify(habitRepository, times(1)).findAllByUser(user);
    }

    @Test
    public void testGenerateHabitStatistics_HabitNotFound() {
        Long habitId = Long.valueOf(UUID.randomUUID().toString());

        when(habitRepository.findByIdAndUser(habitId, user)).thenReturn(Optional.empty());

        String result = habitService.generateHabitStatistics(user, habitId, "weekly");

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, times(1)).findByIdAndUser(habitId, user);
    }

    @Test
    public void testCalculateStreak_HabitNotFound() {
        Long habitId = Long.valueOf(UUID.randomUUID().toString());

        when(habitRepository.findByIdAndUser(habitId, user)).thenReturn(Optional.empty());

        int result = habitService.calculateStreak(user, habitId);

        assertEquals(-1, result);
        verify(habitRepository, times(1)).findByIdAndUser(habitId, user);
    }

    @Test
    public void testCalculateStreak_NoCompletionDates() {
        habit.setCompletionDates(new ArrayList<>()); // Задаем пустой список выполнений
        when(habitRepository.findByIdAndUser(habit.getId(), user)).thenReturn(Optional.of(habit));

        int result = habitService.calculateStreak(user, habit.getId());

        assertEquals(0, result); // Ожидаем, что серий будет 0
        verify(habitRepository, times(1)).findByIdAndUser(habit.getId(), user);
    }

    @Test
    public void testCalculateStreak_Success() {
        List<LocalDate> completionDates = new ArrayList<>();
        completionDates.add(LocalDate.now());
        completionDates.add(LocalDate.now().minusDays(1)); // Предполагаем, что два дня подряд выполнены
        habit.setCompletionDates(completionDates);
        when(habitRepository.findByIdAndUser(habit.getId(), user)).thenReturn(Optional.of(habit));

        int result = habitService.calculateStreak(user, habit.getId());

        assertEquals(2, result); // Ожидаем, что серия будет 2
        verify(habitRepository, times(1)).findByIdAndUser(habit.getId(), user);
    }
}
