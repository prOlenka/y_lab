package com.y_lab.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

//@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		UserRepository userRepository = new UserRepository();
		UserService userService = new UserService(userRepository);
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("\nВыберите действие: ");
			System.out.println("1. Зарегистрироваться");
			System.out.println("2. Войти");
			System.out.println("3. Выйти из программы");

			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume the newline

			if (choice == 1) {
				System.out.print("Введите имя: ");
				String name = scanner.nextLine();
				System.out.print("Введите email: ");
				String email = scanner.nextLine();
				System.out.print("Введите пароль: ");
				String password = scanner.nextLine();

				String result = userService.registerUser(name, email, password);
				System.out.println(result);

			} else if (choice == 2) {
				System.out.print("Введите email: ");
				String email = scanner.nextLine();
				System.out.print("Введите пароль: ");
				String password = scanner.nextLine();

				String result = userService.loginUser(email, password);
				System.out.println(result);

			} else if (choice == 3) {
				System.out.println("Выход из программы.");
				break;
			} else {
				System.out.println("Неверный выбор. Попробуйте снова.");
			}
		}

		scanner.close();
	}
//		SpringApplication.run(ProjectApplication.class, args);

}
