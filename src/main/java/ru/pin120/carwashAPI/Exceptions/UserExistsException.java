package ru.pin120.carwashAPI.Exceptions;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String errorMessage){
        super(errorMessage);
    }
}
