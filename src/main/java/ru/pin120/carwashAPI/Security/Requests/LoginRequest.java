package ru.pin120.carwashAPI.security.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "Необходимо ввести логин")
    private String username;

    @NotBlank(message = "Необходимо ввести пароль")
    private String password;

}
