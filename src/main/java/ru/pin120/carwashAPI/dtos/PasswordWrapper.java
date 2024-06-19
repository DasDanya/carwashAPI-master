package ru.pin120.carwashAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Обертка пароля
 */
@Getter
public class PasswordWrapper {

    /**
     * Пароль
     */
    @NotBlank(message = "Необходимо ввести пароль")
    private String password;
}
