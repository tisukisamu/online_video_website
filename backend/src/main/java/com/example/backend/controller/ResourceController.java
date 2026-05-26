package com.example.backend.controller;

import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
public class ResourceController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> getResource(@PathVariable String type, @PathVariable String filename) {
        return loadResource(Paths.get(type, filename));
    }

    @GetMapping("/{type}/{year}/{month}/{filename:.+}")
    public ResponseEntity<Resource> getDatedResource(
            @PathVariable String type,
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String filename) {
        return loadResource(Paths.get(type, year, month, filename));
    }

    private ResponseEntity<Resource> loadResource(Path relativePath) {
        try {
            Path rootLocation = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path file = rootLocation.resolve(relativePath).normalize();

            // Security check
            if (!file.startsWith(rootLocation)) {
                 throw new ResourceNotFoundException("文件", "path", relativePath.toString());
            }

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = determineContentType(file.getFileName().toString());
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        // inline means display in browser, attachment means download
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new ResourceNotFoundException("文件", "path", relativePath.toString());
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("文件", "path", relativePath.toString());
        }
    }

    private String determineContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".webm")) return "video/webm";
        return "application/octet-stream";
    }
}
