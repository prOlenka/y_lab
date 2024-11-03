package com.y_lab.project.repository;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class HabitRepositoryJdbcImpl implements HabitRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Autowired
    public HabitRepositoryJdbcImpl(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public List<Habit> findAllByUser(UserDTO user) {
        String sql = "SELECT * FROM app_schema.habits WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{user.getId()}, (rs, rowNum) -> mapRowToHabit(rs));
    }

    @Override
    public Optional<Habit> findByIdAndUser(Long id, UserDTO user) {
        String sql = "SELECT * FROM app_schema.habits WHERE id = ? AND user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{id, user.getId()}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToHabit(rs));
            }
            return Optional.empty();
        });
    }

    @Override
    public void deleteByIdAndUser(Long id, UserDTO user) {
        String sql = "DELETE FROM app_schema.habits WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, user.getId());
    }

    @Override
    public void save(Habit habit) {
        String sql = "INSERT INTO app_schema.habits (user_id, name, description, frequency) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, habit.getUser(), habit.getName(), habit.getDescription(), habit.getFrequency());
    }

    private Habit mapRowToHabit(ResultSet rs) throws SQLException {
        Habit habit = new Habit();
        habit.setId(rs.getLong("id"));
        User user = new User(
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getBoolean("is_admin")
        );
        habit.setUser(user);
        habit.setName(rs.getString("name"));
        habit.setDescription(rs.getString("description"));
        habit.setFrequency(rs.getString("frequency"));
        return habit;
    }
}
