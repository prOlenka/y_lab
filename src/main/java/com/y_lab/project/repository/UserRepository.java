package com.y_lab.project.repository;

import com.y_lab.project.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final DataSource dataSource;

    @Autowired
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Лучше использовать логирование
        }
        return Optional.empty();
    }

    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Лучше использовать логирование
        }
        return Optional.empty();
    }

    public void save(User user) {
        String sql = "INSERT INTO users (email, password, name, is_admin) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setBoolean(4, user.isAdmin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Лучше использовать логирование
        }
    }

    public void delete(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Лучше использовать логирование
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Лучше использовать логирование
        }
        return users;
    }

    private User mapToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getBoolean("is_admin")
        );
    }
}
