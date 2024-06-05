package ru.pin120.carwashAPI.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.Exceptions.UserExistsException;
import ru.pin120.carwashAPI.dtos.PasswordWrapper;
import ru.pin120.carwashAPI.dtos.UserDTO;
import ru.pin120.carwashAPI.models.User;
import ru.pin120.carwashAPI.security.requests.LoginRequest;
import ru.pin120.carwashAPI.security.requests.RegisterRequest;
import ru.pin120.carwashAPI.security.responses.JwtResponse;
import ru.pin120.carwashAPI.services.UserService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ValidateInputService validateInputService;
    private final UserService userService;

    public UserController(ValidateInputService validateInputService, UserService userService) {
        this.validateInputService = validateInputService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> get(){
        try{
            List<UserDTO> users= userService.getAll();
            return new ResponseEntity<>(users, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            //UserDTO user = userService.create(registerRequest);
            JwtResponse jwtResponse = userService.create(registerRequest);
            return new ResponseEntity<>(jwtResponse,HttpStatus.OK);

        } catch (Exception e){
            if(e instanceof UserExistsException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            JwtResponse jwtResponse = userService.login(loginRequest);
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof BadCredentialsException){
                return new ResponseEntity<>("Неверный ввод имени пользователя или пароля", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{userName}")
    public ResponseEntity<?> delete(@PathVariable("userName") String userName){
        try{
            Optional<User> userOptional = userService.getByUsName(userName);
            if(userOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Пользователь с именем = %s не найден",userName), HttpStatus.BAD_REQUEST);
            }

            User user = userOptional.get();
            userService.delete(user);

            return ResponseEntity.noContent().build();

        }catch (Exception e){
            if(e instanceof IllegalArgumentException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/editPassword/{userName}")
    public ResponseEntity<?> editPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("userName") String userName, @RequestBody @Valid PasswordWrapper passwordWrapper, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            Optional<User> userOptional = userService.getByUsName(userName);
            if(userOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Пользователь с именем = %s не найден",userName), HttpStatus.BAD_REQUEST);
            }
            User user = userOptional.get();
           return new ResponseEntity<>(userService.editPassword(user,passwordWrapper.getPassword(),authorizationHeader), HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof IllegalArgumentException || e instanceof EntityNotFoundException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
