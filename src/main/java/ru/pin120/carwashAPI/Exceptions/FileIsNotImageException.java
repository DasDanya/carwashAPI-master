package ru.pin120.carwashAPI.Exceptions;

/**
 * Исключение, выбрасываемое в случае, если файл, который должен быть изображением, таковым не является.
 */
public class FileIsNotImageException extends RuntimeException {

    /**
     * Создание нового исключения FileIsNotImageException с указанным сообщением об ошибке
     * @param errorMessage сообщение об ошибке
     */
    public FileIsNotImageException(String errorMessage){
        super(errorMessage);
    }
}
