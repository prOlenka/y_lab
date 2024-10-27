package com.y_lab.project.repository;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.mapper.UserMapper;
import com.y_lab.project.mapper.UserMapperImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HabitRepositoryJdbcImpl implements HabitRepository {

    private final Connection connection;

    public HabitRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Habit> findAllByUser(UserDTO user) {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM app_schema.habits WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Habit habit = mapRowToHabit(rs, user);
                habits.add(habit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return habits;
    }

    @Override
    public Optional<Habit> findByIdAndUser(Long id, UserDTO user) {
        String sql = "SELECT * FROM app_schema.habits WHERE id = ? AND user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.setLong(2, user.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToHabit(rs, user));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void deleteByIdAndUser(Long id, UserDTO user) {
        String sql = "DELETE FROM app_schema.habits WHERE id = ? AND user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.setLong(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Обработка исключений
        }
    }

    @Override
    public void save(Habit habit) {
        String sql = "INSERT INTO app_schema.habits (user_id, name, description, frequency) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, Long.parseLong(habit.getUser().getId()));
            stmt.setString(2, habit.getName());
            stmt.setString(3, habit.getDescription());
            stmt.setString(4, habit.getFrequency());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                habit.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Habit mapRowToHabit(ResultSet rs, UserDTO userDTO) throws SQLException {
        Habit habit = new Habit();
        UserMapper userMapper = new UserMapperImpl();
        habit.setId(rs.getLong("id"));

        User user = userMapper.toEntity(userDTO);
        habit.setUser(user);

        habit.setName(rs.getString("name"));
        habit.setDescription(rs.getString("description"));
        habit.setFrequency(rs.getString("frequency"));
        return habit;
    }


}

