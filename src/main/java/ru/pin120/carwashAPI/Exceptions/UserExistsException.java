package ru.pin120.carwashAPI.Exceptions;

/**
 * Исключение, выбрасываемое в случае, если пользователь уже существует в базе данных
 */
public class UserExistsException extends RuntimeException {

    /**
     * Создание нового исключения UserExistsException с указанным сообщением об ошибке
     * @param errorMessage сообщение об ошибке
     */
    public UserExistsException(String errorMessage){
        super(errorMessage);
    }
}
