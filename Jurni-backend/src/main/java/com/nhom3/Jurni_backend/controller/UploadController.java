package com.nhom3.Jurni_backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final Cloudinary cloudinary;

    public UploadController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Test endpoint to verify Cloudinary connection
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            logger.info("Testing Cloudinary connection...");
            // Try a simple api call to Cloudinary
            Map result = cloudinary.api().resourceTypes(ObjectUtils.asMap());
            logger.info("Cloudinary connection successful");
            return ResponseEntity.ok(Map.of("status", "connected"));
        } catch (Exception e) {
            logger.error("Cloudinary connection failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                logger.warn("Upload attempt with empty file");
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.warn("Upload attempt with non-image file: {}", contentType);
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }

            logger.info("Starting upload to Cloudinary: {}", file.getOriginalFilename());

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "jurni/hotels"
            ));

            String fileUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            logger.info("Upload successful. URL: {}, PublicId: {}", fileUrl, publicId);

            return ResponseEntity.ok(Map.of(
                "url", fileUrl,
                "public_id", publicId,
                "filename", file.getOriginalFilename()
            ));

        } catch (IOException e) {
            logger.error("IOException during upload: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "File read error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during upload: {} - {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
            // Return more detailed error message for debugging
            String details = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "error", "Upload failed: " + e.getMessage(),
                    "details", details,
                    "type", e.getClass().getSimpleName()
                ));
        }
    }
}
