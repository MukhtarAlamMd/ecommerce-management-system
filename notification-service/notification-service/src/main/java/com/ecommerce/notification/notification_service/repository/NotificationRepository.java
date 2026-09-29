package com.ecommerce.notification.notification_service.repository;

import com.ecommerce.notification.notification_service.entity.Notification;
import com.ecommerce.notification.notification_service.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(
            String recipient
    );

    List<Notification> findByUserIdOrderByCreatedAtDesc(
            String userId
    );

    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(
            String userId,
            NotificationStatus status
    );

    long countByUserIdAndStatus(
            String userId,
            NotificationStatus status
    );
}
