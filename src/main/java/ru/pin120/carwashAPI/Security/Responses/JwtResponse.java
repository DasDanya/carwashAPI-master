package ru.pin120.carwashAPI.security.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.pin120.carwashAPI.dtos.UserDTO;
import ru.pin120.carwashAPI.models.User;

@AllArgsConstructor
@Getter
public class JwtResponse {

    private final String type = "Bearer";
    private String token;
    private UserDTO userDTO;
}
