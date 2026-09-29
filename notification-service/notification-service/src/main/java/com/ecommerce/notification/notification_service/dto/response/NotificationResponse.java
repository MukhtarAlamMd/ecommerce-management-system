package com.ecommerce.notification.notification_service.dto.response;

import com.ecommerce.notification.notification_service.enums.NotificationStatus;
import com.ecommerce.notification.notification_service.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private String userId;

    private String recipient;

    private Long orderId;

    private NotificationType type;

    private String title;

    private String message;

    private NotificationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}