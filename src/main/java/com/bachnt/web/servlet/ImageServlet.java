package com.bachnt.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; // Bạn cần import này nếu chưa có
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageServlet extends HttpServlet {

    private String getBaseUploadDirectory() {
        String uploadsDirEnv = System.getenv("UPLOADS_DIR");
        if (uploadsDirEnv != null && !uploadsDirEnv.isEmpty()) {
            return uploadsDirEnv;
        } else {

            System.err.println("Warning: UPLOADS_DIR environment variable is not set. Using local fallback path.");
            return "/Users/ngogiakhanh/Documents/PersonalWebsite/PersonalWebsiteUploads";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestedFileRelativePath = request.getPathInfo();

        if (requestedFileRelativePath == null || requestedFileRelativePath.equals("/") || requestedFileRelativePath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File path not specified.");
            return;
        }

        Path baseDir = Paths.get(getBaseUploadDirectory());
        Path filePath = baseDir.resolve(requestedFileRelativePath.substring(1)); // Bỏ dấu / ở đầu

        if (!Files.exists(filePath) || !Files.isReadable(filePath) || Files.isDirectory(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found or not readable: " + filePath.toString());
            return;
        }

        String contentType = getServletContext().getMimeType(filePath.getFileName().toString());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setContentType(contentType);
        response.setContentLengthLong(Files.size(filePath));

        try (InputStream in = Files.newInputStream(filePath);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.err.println("Error serving file " + filePath.toString() + ": " + e.getMessage());
        }
    }
}