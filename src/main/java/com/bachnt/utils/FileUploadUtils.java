package com.bachnt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.servlet.http.Part;

public class FileUploadUtils {

    private static final String DEFAULT_LOCAL_UPLOADS_DIR = "/Users/ngogiakhanh/Documents/PersonalWebsite/PersonalWebsiteUploads";

    public static String getBaseUploadDirectory() {
        String uploadsDirEnv = System.getenv("UPLOADS_DIR");
        if (uploadsDirEnv != null && !uploadsDirEnv.isEmpty()) {
            return uploadsDirEnv;
        } else {
            System.err.println("Warning: UPLOADS_DIR environment variable is not set. Using local fallback path: " + DEFAULT_LOCAL_UPLOADS_DIR);
            return DEFAULT_LOCAL_UPLOADS_DIR;
        }
    }

    public static String saveUploadedFile(Part filePart, String subfolder) throws IOException {
        if (filePart == null || filePart.getSize() == 0 || filePart.getSubmittedFileName() == null || filePart.getSubmittedFileName().isEmpty()) {
            return null;
        }

        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String fileExtension = "";
        int lastDot = originalFileName.lastIndexOf('.');
        if (lastDot > 0) {
            fileExtension = originalFileName.substring(lastDot);
        }

        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        Path baseDir = Paths.get(getBaseUploadDirectory());
        Path targetDir = baseDir.resolve(subfolder);

        Files.createDirectories(targetDir);

        Path targetFilePath = targetDir.resolve(uniqueFileName);

        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return subfolder + "/" + uniqueFileName;
    }

    public static void deleteUploadedFile(String relativePathToDelete) {
        if (relativePathToDelete == null || relativePathToDelete.isEmpty()) {
            return;
        }
        try {
            Path baseDir = Paths.get(getBaseUploadDirectory());
            Path filePath = baseDir.resolve(relativePathToDelete);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Successfully deleted file: " + filePath.toString());
            } else {
                System.out.println("File to delete does not exist: " + filePath.toString());
            }
        } catch (IOException e) {
            System.err.println("Error deleting physical file " + relativePathToDelete + ": " + e.getMessage());
        }
    }
}