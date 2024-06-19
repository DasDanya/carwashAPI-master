package ru.pin120.carwashAPI.security.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * Запрос на авторизацию
 */
@Getter
public class LoginRequest {
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

}
