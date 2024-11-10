package com.y_lab.project.repository;

public class SqlQueries {
    // HabitRepository
    public static final String FIND_ALL_HABITS_BY_USER = "SELECT * FROM app_schema.habits WHERE user_id = ?";
    public static final String FIND_HABIT_BY_ID_AND_USER = "SELECT * FROM app_schema.habits WHERE id = ? AND user_id = ?";
    public static final String DELETE_HABIT_BY_ID_AND_USER = "DELETE FROM app_schema.habits WHERE id = ? AND user_id = ?";
    public static final String SAVE_HABIT = "INSERT INTO app_schema.habits (user_id, name, description, frequency) VALUES (?, ?, ?, ?)";

    // UserRepository
    public static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    public static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String SAVE_USER = "INSERT INTO users (email, password, name, is_admin) VALUES (?, ?, ?, ?)";
    public static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    public static final String FIND_ALL_USERS = "SELECT * FROM users";
    public static final String FIND_USER_BY_USERNAME = "SELECT * FROM users WHERE name = ?";
}
