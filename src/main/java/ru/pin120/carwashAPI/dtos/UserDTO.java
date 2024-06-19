package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO пользователя
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    /**
     * id пользователя
     */
    private String usId;
    /**
     * Имя пользователя
     */
    private String usName;
    /**
     * Роль пользователя
     */
    private String usRole;
}
