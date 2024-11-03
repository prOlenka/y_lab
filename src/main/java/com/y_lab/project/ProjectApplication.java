package com.y_lab.project;

import com.y_lab.project.interfaces.UserInterface;
import com.y_lab.project.service.HabitService;
import com.y_lab.project.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(UserService userService, HabitService habitService) {
		return args -> {
			UserInterface userInterface = new UserInterface(userService, habitService);
			userInterface.start();
		};
	}
}

