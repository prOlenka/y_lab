package com.y_lab.project.service;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.User;
import com.y_lab.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");
        userDTO.setName("John Doe");
        userDTO.setAdmin(false);
        user = new User(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName(), userDTO.isAdmin());
    }

    @Test
    public void testRegisterUser_Success() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        doNothing().when(userRepository).save(any(User.class));

        String result = userService.registerUser(userDTO);

        assertEquals("Регистрация прошла успешно", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testLoginUser_Success() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.loginUser(userDTO.getEmail(), userDTO.getPassword());

        assertTrue(result.isPresent());
        assertEquals(userDTO.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
    }

    @Test
    public void testLoginUser_Failure() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.loginUser(userDTO.getEmail(), userDTO.getPassword());

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
    }

    @Test
    public void testUpdateUserProfile_Success() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        String result = userService.updateProfile(userDTO, "New Name", "new@example.com");

        assertEquals("Профиль обновлен", result);
        assertEquals("New Name", user.getName());
        assertEquals("new@example.com", user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUserProfile_UserNotFound() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        String result = userService.updateProfile(userDTO, "New Name", "new@example.com");

        assertEquals("Пользователь не найден", result);
        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangePassword_Success() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        String result = userService.changePassword(userDTO, userDTO.getPassword(), "newPassword");

        assertEquals("Пароль изменен", result);
        assertEquals("newPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testChangePassword_UserNotFound() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        String result = userService.changePassword(userDTO, userDTO.getPassword(), "newPassword");

        assertEquals("Пользователь не найден", result);
        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangePassword_OldPasswordIncorrect() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        String result = userService.changePassword(userDTO, "wrongOldPassword", "newPassword");

        assertEquals("Старый пароль неверен", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testIsAdmin_UserIsAdmin() {
        userDTO.setAdmin(true);
        user = new User(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName(), userDTO.isAdmin());
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        boolean result = userService.isAdmin(userDTO);

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
    }

    @Test
    public void testIsAdmin_UserNotFound() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        boolean result = userService.isAdmin(userDTO);

        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
    }
}
