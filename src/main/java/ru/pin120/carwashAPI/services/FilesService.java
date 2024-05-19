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

@Service
public class FilesService {

    public Resource getResource(Path filePath) throws MalformedURLException {
        return new UrlResource(filePath.toUri());
    }

    public byte[] getFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if(Files.exists(path)){
            return Files.readAllBytes(path);
        }else{
            return new byte[0];
        }
    }

    public boolean isImage(MultipartFile photo) {
        String contentType = photo.getContentType();

        return contentType != null && contentType.startsWith("image/");
    }

    public void saveImage(MultipartFile image, String destinationPath) throws IOException {
        byte[] imageAsBytes = image.getBytes();
        Path imagePath = Paths.get(destinationPath);

        Files.write(imagePath,imageAsBytes);
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }
}
