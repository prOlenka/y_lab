package com.y_lab.project.repository;

import com.y_lab.project.entity.User;
import com.y_lab.logging.CustomLogger;
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
    private final CustomLogger logger;

    @Autowired
    public UserRepository(DataSource dataSource, CustomLogger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    public Optional<User> findByEmail(String email) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SqlQueries.FIND_USER_BY_EMAIL)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: " + email, e);
        }
        return Optional.empty();
    }

    public Optional<User> findById(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SqlQueries.FIND_USER_BY_ID)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id: " + id, e);
        }
        return Optional.empty();
    }

    public void save(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SqlQueries.SAVE_USER)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setBoolean(4, user.isAdmin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving user: " + user, e);
        }
    }

    public void delete(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SqlQueries.DELETE_USER)) {
            stmt.setString(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting user: " + user, e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SqlQueries.FIND_ALL_USERS)) {
            while (rs.next()) {
                users.add(mapToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        }
        return users;
    }

    public User findByUsername(String username) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SqlQueries.FIND_USER_BY_USERNAME)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToUser(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: " + username, e);
        }
        return null;
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
