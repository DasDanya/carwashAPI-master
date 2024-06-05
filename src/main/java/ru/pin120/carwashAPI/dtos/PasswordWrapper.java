package ru.pin120.carwashAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordWrapper {

    @NotBlank(message = "Необходимо ввести пароль")
    private String password;
}
