package com.y_lab.project.service;

import com.y_lab.project.entity.User;
import com.y_lab.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private User user;
    private String userId;
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        email = "test@example.com";
        password = "password";
        name = "John Doe";
        isAdmin = false;
        user = createUser( email, password, name, isAdmin);
        userId = UUID.randomUUID().toString();
    }

    private User createUser(String email, String password, String name, boolean isAdmin) {
        return new User( email, password, name, isAdmin);
    }

    @Test
    public void testRegisterUser_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        doNothing().when(userRepository).save(any(User.class));

        String result = userService.registerUser(email, password, name);

        assertEquals("Регистрация прошла успешно", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testLoginUser_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.loginUser(email, password);

        assertEquals("Вход выполнен успешно", result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testLoginUser_Failure() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.loginUser(email, password);

        assertEquals("Неверный логин или пароль", result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testUpdateUserProfile_Success() {
        String userId = "1"; // Пример идентификатора пользователя, используйте правильный формат
        String newEmail = "new@example.com";
        String newName = "John Smith";

        // Создайте пользователя с текущими данными
        User user = new User("old@example.com", "password", "Old Name", false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String result = userService.updateProfile(user, newName, newEmail);

        assertEquals("Профиль обновлен", result);
        assertEquals(newEmail, user.getEmail());
        assertEquals(newName, user.getName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }



    @Test
    public void testUpdateUserProfile_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = userService.updateProfile(user, "New Name", "new@example.com");

        assertEquals("Пользователь не найден", result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangePassword_Success() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        // Создайте пользователя с текущими данными
        User user = new User( "test@example.com", oldPassword, "John Doe", false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String result = userService.changePassword(user, oldPassword, newPassword); // Передайте объект User

        assertEquals("Пароль изменен", result);
        assertEquals(newPassword, user.getPassword());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    public void testChangePassword_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = userService.changePassword(user, "oldPassword", "newPassword");

        assertEquals("Пользователь не найден", result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testIsAdmin_UserIsAdmin() {
        user = createUser( email, password, name, true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.isAdmin(user);

        assertTrue(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testIsAdmin_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.isAdmin(user);

        assertEquals(false, result);
        verify(userRepository, times(1)).findById(userId);
    }
}
