package com.y_lab.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.y_lab.project.service.HabitService;
import com.y_lab.project.service.UserService;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ProjectApplication.class, args);
		UserService userService = context.getBean(UserService.class);
		HabitService habitService = context.getBean(HabitService.class);
		UserInterface userInterface = new UserInterface(userService, habitService);
		userInterface.start();
	}
}
