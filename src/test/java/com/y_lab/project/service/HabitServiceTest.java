package com.y_lab.project.service;

import com.y_lab.project.entity.Habit;
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
import static org.mockito.Mockito.*;

public class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private HabitService habitService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddHabit_Success() {
        String userId = "user123";
        String name = "Exercise";
        String description = "Daily morning exercise";
        String frequency = "ежедневно";

        String result = habitService.addHabit(userId, name, description, frequency);

        assertEquals("Привычка успешно добавлена!", result);
        verify(habitRepository, times(1)).addHabit(eq(userId), any(Habit.class));
    }

    @Test
    public void testUpdateHabit_Success() {
        String userId = "user123";
        String habitId = UUID.randomUUID().toString();
        String newName = "Reading";
        String newDescription = "Read books every evening";
        String newFrequency = "еженедельно";

        Habit existingHabit = new Habit(userId, "Old Name", "Old Description", "ежедневно");
        existingHabit.setId(habitId);

        when(habitRepository.findById(userId, habitId)).thenReturn(Optional.of(existingHabit));
        when(habitRepository.updateHabit(userId, existingHabit)).thenReturn(true);

        String result = habitService.updateHabit(userId, habitId, newName, newDescription, newFrequency);

        assertEquals("Привычка успешно обновлена!", result);
        verify(habitRepository, times(1)).updateHabit(eq(userId), any(Habit.class));
    }

    @Test
    public void testUpdateHabit_HabitNotFound() {
        String userId = "user123";
        String habitId = UUID.randomUUID().toString();

        when(habitRepository.findById(userId, habitId)).thenReturn(Optional.empty());

        String result = habitService.updateHabit(userId, habitId, "New Name", "New Description", "еженедельно");

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, never()).updateHabit(anyString(), any(Habit.class));
    }

    @Test
    public void testDeleteHabit_Success() {
        String userId = "user123";
        String habitId = UUID.randomUUID().toString();

        when(habitRepository.deleteHabit(userId, habitId)).thenReturn(true);

        String result = habitService.deleteHabit(userId, habitId);

        assertEquals("Привычка успешно удалена!", result);
        verify(habitRepository, times(1)).deleteHabit(userId, habitId);
    }

    @Test
    public void testDeleteHabit_HabitNotFound() {
        String userId = "user123";
        String habitId = UUID.randomUUID().toString();

        when(habitRepository.deleteHabit(userId, habitId)).thenReturn(false);

        String result = habitService.deleteHabit(userId, habitId);

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, times(1)).deleteHabit(userId, habitId);
    }

    @Test
    public void testListUserHabits() {
        String userId = "user123";
        List<Habit> habits = new ArrayList<>();
        habits.add(new Habit(userId, "Exercise", "Morning run", "ежедневно"));
        habits.add(new Habit(userId, "Meditation", "10 minutes of meditation", "ежедневно"));

        when(habitRepository.findAllByUserId(userId)).thenReturn(habits);

        List<Habit> result = habitService.listUserHabits(userId);

        assertEquals(2, result.size());
        assertEquals("Exercise", result.get(0).getName());
        assertEquals("Meditation", result.get(1).getName());
        verify(habitRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGenerateHabitStatistics_HabitNotFound() {
        String userId = "user123";
        String habitId = UUID.randomUUID().toString();

        when(habitRepository.findById(userId, habitId)).thenReturn(Optional.empty());

        String result = habitService.generateHabitStatistics(userId, habitId, "weekly");

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, times(1)).findById(userId, habitId);
    }

    @Test
    public void testCalculateStreak_HabitNotFound() {
        String userId = "user123";
        String habitId = UUID.randomUUID().toString();

        when(habitRepository.findById(userId, habitId)).thenReturn(Optional.empty());

        int result = habitService.calculateStreak(userId, habitId);

        assertEquals(-1, result);
        verify(habitRepository, times(1)).findById(userId, habitId);
    }
}
