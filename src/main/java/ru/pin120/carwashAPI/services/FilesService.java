package ru.pin120.carwashAPI.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис для работы с файлами
 */
@Service
public class FilesService {

    /**
     * Получение файла
     * @param filePath путь до файла
     * @return Файл в виде массива байт
     * @throws IOException если возникает ошибка при чтении файла
     */
    public byte[] getFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if(Files.exists(path)){
            return Files.readAllBytes(path);
        }else{
            return new byte[0];
        }
    }

    /**
     * Проверяет, является ли файл изображением на основе типа содержимого MultipartFile
     * @param photo MultipartFile, представляющий изображение
     * @return true, если файл является изображением, иначе false
     */
    public boolean isImage(MultipartFile photo) {
        String contentType = photo.getContentType();

        return contentType != null && contentType.startsWith("image/");
    }


    /**
     * Сохраняет изображение в указанный путь.
     *
     * @param image  MultipartFile, представляющий изображение
     * @param destinationPath путь для сохранения изображения
     * @throws IOException если возникает ошибка при сохранении изображения
     */
    public void saveImage(MultipartFile image, String destinationPath) throws IOException {
        byte[] imageAsBytes = image.getBytes();
        Path imagePath = Paths.get(destinationPath);

        Files.write(imagePath,imageAsBytes);
    }

    /**
     * Удаляет файл по указанному пути, если он существует.
     *
     * @param filePath путь к файлу, который нужно удалить
     * @throws IOException если возникает ошибка при удалении файла
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }
}
