package ru.pin120.carwashAPI.security.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank(message = "Необходимо ввести логин")
    private String username;

    @NotBlank(message = "Необходимо ввести пароль")
    private String password;

    @NotBlank(message = "Необходимо указать роль пользователя")
    private String role;
}
