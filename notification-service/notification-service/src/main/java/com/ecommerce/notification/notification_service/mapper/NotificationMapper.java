package com.ecommerce.notification.notification_service.mapper;

import com.ecommerce.notification.notification_service.dto.request.CreateNotificationRequest;
import com.ecommerce.notification.notification_service.dto.response.NotificationResponse;
import com.ecommerce.notification.notification_service.entity.Notification;
import com.ecommerce.notification.notification_service.enums.NotificationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public Notification toEntity(CreateNotificationRequest request) {

        return Notification.builder()
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .orderId(request.getOrderId())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .recipient(notification.getRecipient())
                .orderId(notification.getOrderId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}