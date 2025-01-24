package com.vendor.service;

import com.vendor.controller.VendorDocumentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DocumentStorageService {

    private static final Logger logger = LoggerFactory.getLogger(VendorDocumentController.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, Long vendorId, String vendorName,
                            String documentType) throws IOException {
        // Create directory with vendor name and ID
        String vendorDir = uploadDir + "/" + vendorName + "_" + vendorId;
        Files.createDirectories(Paths.get(vendorDir));

        // Generate filename with vendor name and document type
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String storedFilename = vendorName + "_" + documentType + "_" + timestamp + extension;

        // Store file
        Path targetLocation = Paths.get(vendorDir).resolve(storedFilename);
        Files.copy(file.getInputStream(), targetLocation);

        return targetLocation.toString();
    }

//    public byte[] retrieveFile(String filePath) throws IOException {
//        Path path = Paths.get(filePath);
//        return Files.readAllBytes(path);
//    }
private void validateFilePath(String filePath) {
    Path path = Paths.get(filePath);

    // Check if path is within upload directory
    Path uploadDirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    Path normalizedPath = path.toAbsolutePath().normalize();

    if (!normalizedPath.startsWith(uploadDirPath)) {
        logger.error("Attempted to access file outside upload directory: {}", filePath);
        throw new RuntimeException("Invalid file path");
    }
}
public byte[] retrieveFile(String filePath) {
    try {
        logger.debug("Attempting to retrieve file from path: {}", filePath);

        validateFilePath(filePath);
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            logger.error("File not found at path: {}", filePath);
            throw new RuntimeException("File not found at path: " + filePath);
        }

        byte[] content = Files.readAllBytes(path);

        if (content == null || content.length == 0) {
            logger.error("Retrieved empty content for file: {}", filePath);
            throw new RuntimeException("File content is empty");
        }

        logger.debug("Successfully retrieved file: {}, size: {} bytes", filePath, content.length);
        return content;
    } catch (IOException e) {
        logger.error("Failed to read file from path: {}", filePath, e);
        throw new RuntimeException("Failed to read file: " + e.getMessage());
    } catch (Exception e) {
        logger.error("Failed to retrieve file from path: {}", filePath, e);
        throw new RuntimeException("Failed to retrieve file: " + e.getMessage());
    }
}
}