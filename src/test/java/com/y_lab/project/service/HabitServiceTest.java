package com.y_lab.project.service;

import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.mapper.HabitMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

public class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitMapper habitMapper;

    @InjectMocks
    private HabitService habitService;

    private UserDTO userDTO;
    private User user;
    private Habit habit;
    private HabitDTO habitDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setId(123L);

        user = new User();
        user.setId("user123");

        habit = new Habit(user, "Exercise", "Daily morning exercise", "ежедневно");
        habitDTO = new HabitDTO();
        habitDTO.setName("Exercise");
        habitDTO.setDescription("Daily morning exercise");
        habitDTO.setFrequency("ежедневно");
    }

    @Test
    public void testAddHabit_Success() {
        when(habitMapper.toEntity(habitDTO)).thenReturn(habit);

        String result = habitService.addHabit(habitDTO);

        assertEquals("Привычка успешно добавлена!", result);
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    public void testUpdateHabit_Success() {
        String newName = "Reading";
        String newDescription = "Read books every evening";
        String newFrequency = "еженедельно";

        habitDTO.setName(newName);
        habitDTO.setDescription(newDescription);
        habitDTO.setFrequency(newFrequency);

        when(habitRepository.findByIdAndUser(habit.getId(), userDTO)).thenReturn(Optional.of(habit));

        String result = habitService.updateHabit(userDTO, habit.getId(), habitDTO);

        assertEquals("Привычка успешно обновлена!", result);
        assertEquals(newName, habit.getName());
        assertEquals(newDescription, habit.getDescription());
        assertEquals(newFrequency, habit.getFrequency());
        verify(habitRepository, times(1)).save(habit);
    }

    @Test
    public void testUpdateHabit_HabitNotFound() {
        when(habitRepository.findByIdAndUser(habit.getId(), userDTO)).thenReturn(Optional.empty());

        String result = habitService.updateHabit(userDTO, habit.getId(), habitDTO);

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    public void testDeleteHabit_Success() {
        when(habitRepository.findByIdAndUser(habit.getId(), userDTO)).thenReturn(Optional.of(habit));
        String result = habitService.deleteHabit(userDTO, habit.getId());

        assertEquals("Привычка успешно удалена!", result);
        verify(habitRepository, times(1)).deleteByIdAndUser(habit.getId(), userDTO);
    }

    @Test
    public void testDeleteHabit_HabitNotFound() {
        when(habitRepository.findByIdAndUser(habit.getId(), userDTO)).thenReturn(Optional.empty());

        String result = habitService.deleteHabit(userDTO, habit.getId());

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, never()).deleteByIdAndUser(any(Long.class), any(UserDTO.class));
    }

    @Test
    public void testListUserHabits() {
        List<Habit> habits = new ArrayList<>();
        habits.add(new Habit(user, "Exercise", "Morning run", "ежедневно"));
        habits.add(new Habit(user, "Meditation", "10 minutes of meditation", "ежедневно"));

        when(habitRepository.findAllByUser(userDTO)).thenReturn(habits);
        when(habitMapper.toHabitDTO(any(Habit.class))).thenAnswer(invocation -> {
            Habit h = invocation.getArgument(0);
            HabitDTO dto = new HabitDTO();
            dto.setName(h.getName());
            dto.setDescription(h.getDescription());
            dto.setFrequency(h.getFrequency());
            return dto;
        });

        List<HabitDTO> result = habitService.listUserHabits(userDTO);

        assertEquals(2, result.size());
        assertEquals("Exercise", result.get(0).getName());
        assertEquals("Meditation", result.get(1).getName());
        verify(habitRepository, times(1)).findAllByUser(userDTO);
    }

    @Test
    public void testGenerateHabitStatistics_HabitNotFound() {
        Long habitId = habit.getId();

        when(habitRepository.findByIdAndUser(habitId, userDTO)).thenReturn(Optional.empty());

        String result = habitService.generateHabitStatistics(userDTO, habitId, "weekly");

        assertEquals("Привычка не найдена", result);
        verify(habitRepository, times(1)).findByIdAndUser(habitId, userDTO);
    }

    @Test
    public void testCalculateStreak_HabitNotFound() {
        Long habitId = habit.getId();

        when(habitRepository.findByIdAndUser(habitId, userDTO)).thenReturn(Optional.empty());

        int result = habitService.calculateStreak(userDTO, habitId);

        assertEquals(-1, result);
        verify(habitRepository, times(1)).findByIdAndUser(habitId, userDTO);
    }

    @Test
    public void testCalculateStreak_NoCompletionDates() {
        habit.setCompletionDates(new ArrayList<>());
        when(habitRepository.findByIdAndUser(habit.getId(), userDTO)).thenReturn(Optional.of(habit));

        int result = habitService.calculateStreak(userDTO, habit.getId());

        assertEquals(0, result);
        verify(habitRepository, times(1)).findByIdAndUser(habit.getId(), userDTO);
    }

    @Test
    public void testCalculateStreak_Success() {
        List<LocalDate> completionDates = new ArrayList<>();
        completionDates.add(LocalDate.now());
        completionDates.add(LocalDate.now().minusDays(1));
        habit.setCompletionDates(completionDates);
        when(habitRepository.findByIdAndUser(habit.getId(), userDTO)).thenReturn(Optional.of(habit));

        int result = habitService.calculateStreak(userDTO, habit.getId());

        assertEquals(2, result);
        verify(habitRepository, times(1)).findByIdAndUser(habit.getId(), userDTO);
    }
}
