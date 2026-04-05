package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Notification> findByUserIdAndIsRead(String userId, Boolean isRead);
}
