package com.y_lab.project.service;
import com.y_lab.project.entity.User;
import com.y_lab.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.doNothing;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        String userId = UUID.randomUUID().toString();
        String email = "test@example.com";
        String password = "password";
        String name = "John Doe";
        boolean isAdmin = false;

        User user = new User(userId, email, password, name, isAdmin);

        doNothing().when(userRepository).save(any(User.class));

        String result = userService.registerUser(email, password, name);

        assertEquals("Пользователь успешно зарегистрирован!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    public void testLoginUser_Success() {
        String userId = UUID.randomUUID().toString();
        String email = "test@example.com";
        String password = "password";
        String name = "John Doe";
        boolean isAdmin = false;

        User user = new User(userId, email, password, name, isAdmin);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        String result = userService.loginUser(email, password);

        assertEquals("Вход выполнен успешно!", result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testLoginUser_Failure() {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        String result = userService.loginUser(email, password);

        assertEquals("Неверный логин или пароль", result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testUpdateUserProfile_Success() {
        String userId = UUID.randomUUID().toString();
        String email = "test@example.com";
        String password = "password";
        String name = "John Doe";
        boolean isAdmin = false;

        User user = new User(userId, email, password, name, isAdmin);
        String newEmail = "new@example.com";
        String newName = "John Smith";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String result = userService.updateProfile(userId, newName, newEmail);

        assertEquals("Профиль обновлен успешно", result);
        assertEquals(newEmail, user.getEmail());
        assertEquals(newName, user.getName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUserProfile_UserNotFound() {
        String userId = UUID.randomUUID().toString();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = userService.updateProfile(userId, "New Name", "new@example.com");

        assertEquals("Пользователь не найден", result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangePassword_Success() {
        String userId = UUID.randomUUID().toString();
        String email = "test@example.com";
        String password = "password";
        String name = "John Doe";
        boolean isAdmin = false;

        User user = new User(userId, email, password, name, isAdmin);
        String newPassword = "newPassword";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String result = userService.changePassword(userId,password, newPassword);

        assertEquals("Пароль изменен успешно", result);
        assertEquals(newPassword, user.getPassword());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testChangePassword_UserNotFound() {
        String userId = UUID.randomUUID().toString();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = userService.changePassword(userId,"oldPassword", "newPassword");

        assertEquals("Пользователь не найден", result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testIsAdmin_UserIsAdmin() {
        String userId = UUID.randomUUID().toString();
        String email = "admin@example.com";
        String password = "adminpassword";
        String name = "Admin";
        boolean isAdmin = true;

        User user = new User(userId, email, password, name, isAdmin);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.isAdmin(userId);

        assertTrue(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testIsAdmin_UserNotFound() {
        String userId = UUID.randomUUID().toString();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.isAdmin(userId);

        assertEquals(false, result);
        verify(userRepository, times(1)).findById(userId);
    }
}
