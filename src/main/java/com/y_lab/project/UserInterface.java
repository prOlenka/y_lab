package com.y_lab.project;

import com.y_lab.project.entity.User;
import com.y_lab.project.service.HabitService;
import com.y_lab.project.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class UserInterface {
    private final UserService userService;
    private final HabitService habitService;
    private String loggedInUserId;

    public UserInterface(UserService userService, HabitService habitService) {
        this.userService = userService;
        this.habitService = habitService;
        this.loggedInUserId = null;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nВыберите действие: ");
            if (loggedInUserId == null) {
                showMainMenu(scanner);
            } else {
                showUserMenu(scanner);
            }
        }
    }

    private void showMainMenu(Scanner scanner) {
        System.out.println("1. Зарегистрироваться");
        System.out.println("2. Войти");
        System.out.println("3. Выйти из программы");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                registerUser(scanner);
                break;
            case 2:
                loginUser(scanner);
                break;
            case 3:
                System.out.println("Выход из программы");
                System.exit(0);
            default:
                System.out.println("Некорректный выбор. Пожалуйста, выберите 1, 2 или 3");
        }
    }

    private void registerUser(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        String registrationResult = userService.registerUser(email, password, name);
        System.out.println(registrationResult);
    }

    private void loginUser(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        loggedInUserId = userService.loginUser(email, password);
        if (Objects.equals(loggedInUserId, "Неверный пароль") || Objects.equals(loggedInUserId, "Пользователь с таким email не найден")) {
            System.out.println(loggedInUserId);
            loggedInUserId = null; // сбросить ID, если вход не удался
        } else {
            System.out.println("Вход выполнен успешно");
        }
    }

    private void showUserMenu(Scanner scanner) {
        System.out.println("1. Редактировать профиль");
        System.out.println("2. Изменить пароль");
        System.out.println("3. Удалить аккаунт");
        System.out.println("4. Добавить привычку");
        System.out.println("5. Редактировать привычку");
        System.out.println("6. Удалить привычку");
        System.out.println("7. Показать привычки");
        System.out.println("8. Статистика привычек");
        System.out.println("9. Выйти из аккаунта");
        if (userService.isAdmin(loggedInUserId)) {
            System.out.println("10. Показать всех пользователей и их привычки");
        }

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                updateProfile(scanner);
                break;
            case 2:
                changePassword(scanner);
                break;
            case 3:
                deleteAccount(scanner);
                break;
            case 4:
                addHabit(scanner);
                break;
            case 5:
                editHabit(scanner);
                break;
            case 6:
                deleteHabit(scanner);
                break;
            case 7:
                listUserHabits();
                break;
            case 8:
                showStatistics(scanner);
                break;
            case 9:
                loggedInUserId = null;
                System.out.println("Вы вышли из аккаунта");
                break;
            case 10:
                if (userService.isAdmin(loggedInUserId)) {
                    listAllUsersAndHabits();
                } else {
                    System.out.println("Доступ запрещен: только администраторы могут выполнять эту операцию");
                }
                break;
            default:
                System.out.println("Неверный выбор. Попробуйте снова");
                break;
        }
    }

    private void updateProfile(Scanner scanner) {
        System.out.print("Введите новое имя: ");
        String newName = scanner.nextLine();
        System.out.print("Введите новый email: ");
        String newEmail = scanner.nextLine();
        String updateProfileResult = userService.updateProfile(loggedInUserId, newName, newEmail);
        System.out.println(updateProfileResult);
    }

    private void changePassword(Scanner scanner) {
        System.out.print("Введите старый пароль: ");
        String oldPassword = scanner.nextLine();
        System.out.print("Введите новый пароль: ");
        String newPassword = scanner.nextLine();
        String changePasswordResult = userService.changePassword(loggedInUserId, oldPassword, newPassword);
        System.out.println(changePasswordResult);
    }

    private void deleteAccount(Scanner scanner) {
        System.out.print("Вы уверены, что хотите удалить аккаунт? (да/нет): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("да")) {
            String deleteResult = userService.deleteUser(loggedInUserId);
            System.out.println(deleteResult);
            loggedInUserId = null; // Пользователь вышел из системы
        }
    }

    private void addHabit(Scanner scanner) {
        System.out.print("Введите название привычки: ");
        String habitName = scanner.nextLine();
        System.out.print("Введите описание привычки: ");
        String habitDescription = scanner.nextLine();
        System.out.print("Введите частоту (ежедневно/еженедельно): ");
        String habitFrequency = scanner.nextLine();
        String addHabitResult = habitService.addHabit(loggedInUserId, habitName, habitDescription, habitFrequency);
        System.out.println(addHabitResult);
    }

    private void editHabit(Scanner scanner) {
        System.out.print("Введите ID привычки, которую хотите изменить: ");
        String habitIdToEdit = scanner.nextLine();
        System.out.print("Введите новое название привычки: ");
        String newHabitName = scanner.nextLine();
        System.out.print("Введите новое описание привычки: ");
        String newHabitDescription = scanner.nextLine();
        System.out.print("Введите новую частоту (ежедневно/еженедельно): ");
        String newHabitFrequency = scanner.nextLine();
        String updateHabitResult = habitService.updateHabit(habitIdToEdit, loggedInUserId, newHabitName, newHabitDescription, newHabitFrequency);
        System.out.println(updateHabitResult);
    }

    private void deleteHabit(Scanner scanner) {
        System.out.print("Введите ID привычки, которую хотите удалить: ");
        String habitIdToDelete = scanner.nextLine();
        String deleteHabitResult = habitService.deleteHabit(habitIdToDelete, loggedInUserId);
        System.out.println(deleteHabitResult);
    }

    private void listUserHabits() {
        System.out.println("Ваши привычки:");
        habitService.listUserHabits(loggedInUserId).forEach(habit -> {
            System.out.println("ID: " + habit.getId());
            System.out.println("Название: " + habit.getName());
            System.out.println("Описание: " + habit.getDescription());
            System.out.println("Частота: " + habit.getFrequency());
        });
    }

    private void showStatistics(Scanner scanner) {
        System.out.println("Выберите тип статистики:");
        System.out.println("1. Подсчет текущих серий выполнения привычек (streak)");
        System.out.println("2. Процент успешного выполнения привычек за определенный период");
        System.out.println("3. Отчет по прогрессу выполнения");

        int statsChoice = scanner.nextInt();
        scanner.nextLine();

        switch (statsChoice) {
            case 1:
                calculateStreak(scanner);
                break;
            case 2:
                calculateSuccessPercentage(scanner);
                break;
            case 3:
                generateProgressReport();
                break;
            default:
                System.out.println("Некорректный выбор. Пожалуйста, выберите 1, 2 или 3.");
                break;
        }
    }

    private void calculateStreak(Scanner scanner) {
        System.out.print("Введите название привычки: ");
        String streakHabitName = scanner.nextLine();
        Optional<String> streakHabitIdOpt = habitService.findHabitIdByName(loggedInUserId, streakHabitName);

        if (streakHabitIdOpt.isEmpty()) {
            System.out.println("Привычка с таким названием не найдена");
        } else {
            int streak = habitService.calculateStreak(loggedInUserId, streakHabitIdOpt.get());
            if (streak == -1) {
                System.out.println("Привычка не найдена");
            } else {
                System.out.println("Текущая серия выполнения привычки: " + streak + " дней");
            }
        }
    }

    private void calculateSuccessPercentage(Scanner scanner) {
        System.out.print("Введите название привычки: ");
        String sHabitName = scanner.nextLine();
        Optional<String> habitIdOpt = habitService.findHabitIdByName(loggedInUserId, sHabitName);

        if (habitIdOpt.isEmpty()) {
            System.out.println("Привычка с таким названием не найдена");
        } else {
            System.out.print("Введите период (например, 'неделя', 'месяц'): ");
            String period = scanner.nextLine();
            String habitStatistics = habitService.generateHabitStatistics(loggedInUserId, habitIdOpt.get(), period);
            System.out.println(habitStatistics);
        }
    }

    private void generateProgressReport() {
        System.out.println("Отчет по всем вашим привычкам:");
        habitService.listUserHabits(loggedInUserId).forEach(habit -> {
            System.out.println("ID: " + habit.getId());
            System.out.println("Название: " + habit.getName());
            System.out.println("Описание: " + habit.getDescription());
            System.out.println("Частота: " + habit.getFrequency());
            System.out.println("Процент выполнения: " + habit.getStatistics("все время"));
            System.out.println("Текущий streak: " + habitService.calculateStreak(loggedInUserId, habit.getId()) + " дней");
        });
    }

    private void listAllUsersAndHabits() {
        List<User> allUsers = userService.getAllUsers(loggedInUserId);
        for (User user : allUsers) {
            System.out.println("ID: " + user.getId());
            System.out.println("Имя: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Привычки:");
            habitService.listUserHabits(user.getId()).forEach(habit -> {
                System.out.println("  - ID: " + habit.getId());
                System.out.println("  - Название: " + habit.getName());
                System.out.println("  - Описание: " + habit.getDescription());
                System.out.println("  - Частота: " + habit.getFrequency());
            });
        }
    }
}

