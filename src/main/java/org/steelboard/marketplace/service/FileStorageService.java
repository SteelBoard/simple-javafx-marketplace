package org.steelboard.marketplace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Path UPLOAD_ROOT =
            Paths.get(System.getProperty("user.dir"), "uploads");

    public String saveFile(MultipartFile file, String subDir) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        try {
            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;

            Path targetDir = UPLOAD_ROOT.resolve(subDir);
            Files.createDirectories(targetDir); 

            Path targetFile = targetDir.resolve(fileName);
            file.transferTo(targetFile.toFile());

            
            return "/uploads/" + subDir + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла", e);
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
