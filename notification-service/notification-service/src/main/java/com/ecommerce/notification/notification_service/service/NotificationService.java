package com.ecommerce.notification.notification_service.service;

import com.ecommerce.notification.notification_service.dto.event.NotificationEvent;
import com.ecommerce.notification.notification_service.dto.request.CreateNotificationRequest;
import com.ecommerce.notification.notification_service.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(
            CreateNotificationRequest request
    );

    NotificationResponse createNotificationFromEvent(
            NotificationEvent event
    );

    NotificationResponse getNotificationById(
            Long id
    );

    List<NotificationResponse> getAllNotifications();

    List<NotificationResponse> getUserNotifications(
            String userEmail
    );

    List<NotificationResponse> getNotificationsByUserId(
            String userId
    );

    List<NotificationResponse> getUnreadNotifications(
            String userId
    );

    NotificationResponse markAsRead(
            Long id
    );

    void markAllAsRead(
            String userId
    );

    void deleteNotification(
            Long id
    );

    long getUnreadNotificationCount(
            String userId
    );
}