package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Notification;
import com.nhom3.Jurni_backend.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> listNotifications(
            @RequestParam(required = false) String userId) {
        if (userId != null) {
            return ResponseEntity.ok(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId));
        }
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        return ResponseEntity.status(201).body(notificationRepository.save(notification));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        return notificationRepository.findById(id).map(n -> {
            n.setIsRead(true);
            return ResponseEntity.ok(notificationRepository.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id) {
        return notificationRepository.findById(id).map(n -> {
            notificationRepository.delete(n);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
