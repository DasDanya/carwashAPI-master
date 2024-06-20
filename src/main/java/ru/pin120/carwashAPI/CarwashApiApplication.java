package ru.pin120.carwashAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Главный класс приложения для запуска Spring Boot приложения "Carwash API".
 * Используется для инициализации и запуска приложения с помощью Spring Boot.
 */
@SpringBootApplication
public class CarwashApiApplication extends SpringBootServletInitializer {

	/**
	 * Точка входа в приложение Spring Boot.
	 *
	 * @param args Аргументы командной строки.
	 */
	public static void main(String[] args) {
		SpringApplication.run(CarwashApiApplication.class, args);
	}

}


