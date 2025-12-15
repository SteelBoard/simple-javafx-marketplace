package org.steelboard.marketplace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    public String saveFile(MultipartFile file, String directory) {
        try {
            String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
            String fileName = UUID.randomUUID() + "." + fileExtension;
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            return filePath.toString(); // путь можно сохранить в БД
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла", e);
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
