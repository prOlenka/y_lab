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

    @Autowired
    public HabitRepositoryJdbcImpl(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Habit> findAllByUser(UserDTO user) {
        return jdbcTemplate.query(SqlQueries.FIND_ALL_HABITS_BY_USER, new Object[]{user.getId()}, (rs, rowNum) -> mapRowToHabit(rs));
    }

    @Override
    public Optional<Habit> findByIdAndUser(Long id, UserDTO user) {
        return jdbcTemplate.query(SqlQueries.FIND_HABIT_BY_ID_AND_USER, new Object[]{id, user.getId()}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToHabit(rs));
            }
            return Optional.empty();
        });
    }

    @Override
    public void deleteByIdAndUser(Long id, UserDTO user) {
        jdbcTemplate.update(SqlQueries.DELETE_HABIT_BY_ID_AND_USER, id, user.getId());
    }

    @Override
    public void save(Habit habit) {
        jdbcTemplate.update(SqlQueries.SAVE_HABIT, habit.getUser().getId(), habit.getName(), habit.getDescription(), habit.getFrequency());
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
