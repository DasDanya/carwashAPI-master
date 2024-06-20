package ru.pin120.carwashAPI.security.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.pin120.carwashAPI.dtos.UserDTO;
import ru.pin120.carwashAPI.models.User;

/**
 * Класс с информацией о клиенте и JWT токене
 */
@AllArgsConstructor
@Getter
public class JwtResponse {

    /**
     * Схема аутентификации
     */
    private final String type = "Bearer";
    /**
     * JWT токен
     */
    private String token;
    /**
     * Данные о пользователе
     */
    private UserDTO userDTO;
}
