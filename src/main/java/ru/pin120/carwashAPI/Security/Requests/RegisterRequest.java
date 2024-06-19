package ru.pin120.carwashAPI.security.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Запрос на регистрацию
 */
@Getter
public class RegisterRequest {

    /**
     * Имя пользователя
     */
    @NotBlank(message = "Необходимо ввести логин")
    private String username;

    /**
     * Пароль
     */
    @NotBlank(message = "Необходимо ввести пароль")
    private String password;

    /**
     * Роль
     */
    @NotBlank(message = "Необходимо указать роль пользователя")
    private String role;
}
