package com.bachnt.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

public class ImageServlet extends HttpServlet {
    private static final String BASE_UPLOAD_DIR = "/Users/ngogiakhanh/Documents/PersonalWebsite/PersonalWebsiteUploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String requestedFile = request.getPathInfo();
        
        if (requestedFile == null || requestedFile.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        File file = new File(BASE_UPLOAD_DIR, requestedFile);
        
        if (!file.exists() || !file.canRead()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        
        response.setContentLength((int) file.length());
        
        try (InputStream in = Files.newInputStream(file.toPath());
             OutputStream out = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
