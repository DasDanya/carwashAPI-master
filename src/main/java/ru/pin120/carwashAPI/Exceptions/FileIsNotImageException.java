package ru.pin120.carwashAPI.Exceptions;

public class FileIsNotImageException extends RuntimeException {

    public FileIsNotImageException(String errorMessage){
        super(errorMessage);
    }
}
