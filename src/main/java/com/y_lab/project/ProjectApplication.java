package com.y_lab.project;

import com.y_lab.project.repository.HabitRepositoryJdbcImpl;
import com.y_lab.project.repository.UserRepository;
import com.y_lab.project.repository.HabitRepository;
import com.y_lab.project.service.HabitService;
import com.y_lab.project.service.UserService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProjectApplication {

	public static void main(String[] args) {
		String url = "jdbc:postgresql://localhost:5432/y_lab";
		String user = "username";
		String password = "password";

		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			UserRepository userRepository = new UserRepository(connection);
			HabitRepository habitRepository = new HabitRepositoryJdbcImpl(connection);
			UserService userService = new UserService(userRepository);
			HabitService habitService = new HabitService(habitRepository);

			UserInterface userInterface = new UserInterface(userService, habitService);
			userInterface.start();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

