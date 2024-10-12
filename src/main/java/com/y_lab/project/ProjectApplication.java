package com.y_lab.project;

import com.y_lab.project.repository.HabitRepository;
import com.y_lab.project.repository.UserRepository;
import com.y_lab.project.service.HabitService;
import com.y_lab.project.service.UserService;

public class ProjectApplication {

	public static void main(String[] args) {
			UserRepository userRepository = new UserRepository();
			UserService userService = new UserService(userRepository);
			HabitRepository habitRepository = new HabitRepository();
			HabitService habitService = new HabitService(habitRepository);

			UserInterface userInterface = new UserInterface(userService, habitService);
			userInterface.start();
		}
	}
