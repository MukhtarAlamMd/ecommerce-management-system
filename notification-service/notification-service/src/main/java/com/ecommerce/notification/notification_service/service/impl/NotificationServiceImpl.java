package com.ecommerce.notification.notification_service.service.impl;

import com.ecommerce.notification.notification_service.dto.event.NotificationEvent;
import com.ecommerce.notification.notification_service.dto.request.CreateNotificationRequest;
import com.ecommerce.notification.notification_service.dto.response.NotificationResponse;
import com.ecommerce.notification.notification_service.entity.Notification;
import com.ecommerce.notification.notification_service.enums.NotificationStatus;
import com.ecommerce.notification.notification_service.enums.NotificationType;
import com.ecommerce.notification.notification_service.exception.ResourceNotFoundException;
import com.ecommerce.notification.notification_service.mapper.NotificationMapper;
import com.ecommerce.notification.notification_service.repository.NotificationRepository;
import com.ecommerce.notification.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {


    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;


    // ============================================================
    // CREATE NOTIFICATION
    // ============================================================

    @Override
    public NotificationResponse createNotification(
            CreateNotificationRequest request) {

        Notification notification =
                notificationMapper.toEntity(request);

        Notification savedNotification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(
                savedNotification
        );
    }


    // ============================================================
    // GET NOTIFICATION BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(
            Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + id
                                )
                        );

        return notificationMapper.toResponse(
                notification
        );
    }


    // ============================================================
    // GET ALL NOTIFICATIONS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {

        return notificationRepository
                .findAll()
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }


    // ============================================================
    // GET USER NOTIFICATIONS BY EMAIL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(
            String userEmail) {

        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(
                        userEmail
                )
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }


    // ============================================================
    // GET USER NOTIFICATIONS BY USER ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(
            String userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }


    // ============================================================
    // GET UNREAD NOTIFICATIONS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(
            String userId) {

        return notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        NotificationStatus.UNREAD
                )
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }


    // ============================================================
    // MARK ONE NOTIFICATION AS READ
    // ============================================================

    @Override
    public NotificationResponse markAsRead(
            Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + id
                                )
                        );

        notification.setStatus(
                NotificationStatus.READ
        );

        notification.setReadAt(
                LocalDateTime.now()
        );

        Notification updatedNotification =
                notificationRepository.save(
                        notification
                );

        return notificationMapper.toResponse(
                updatedNotification
        );
    }


    // ============================================================
    // CREATE NOTIFICATION FROM KAFKA EVENT
    // ============================================================

    @Override
    public NotificationResponse createNotificationFromEvent(
            NotificationEvent event) {

        System.out.println(
                "========== KAFKA EVENT RECEIVED =========="
        );

        System.out.println(
                "userId     = " + event.getUserId()
        );

        System.out.println(
                "recipient  = " + event.getRecipient()
        );

        System.out.println(
                "orderId    = " + event.getOrderId()
        );

        System.out.println(
                "type       = " + event.getType()
        );

        System.out.println(
                "title      = " + event.getTitle()
        );

        System.out.println(
                "message    = " + event.getMessage()
        );

        System.out.println(
                "=========================================="
        );

        CreateNotificationRequest request =
                CreateNotificationRequest.builder()
                        .userId(event.getUserId())
                        .recipient(event.getRecipient())
                        .orderId(event.getOrderId())
                        .type(parseNotificationType(event.getType()))
                        .title(event.getTitle())
                        .message(event.getMessage())
                        .build();

        NotificationResponse response =
                createNotification(request);

        System.out.println(
                "========== NOTIFICATION SAVED =========="
        );

        System.out.println(
                "notificationId = " + response.getId()
        );

        System.out.println(
                "========================================"
        );

        return response;
    }


/*

    @Override
    public NotificationResponse createNotificationFromEvent(
            NotificationEvent event) {

        CreateNotificationRequest request =
                CreateNotificationRequest.builder()
                        .userId(
                                event.getUserId()
                        )
                        .recipient(
                                event.getRecipient()
                        )

                        // IMPORTANT:
                        // Use orderId directly from Kafka event.
                        .orderId(
                                event.getOrderId()
                        )

                        .type(
                                parseNotificationType(
                                        event.getType()
                                )
                        )
                        .title(
                                event.getTitle()
                        )
                        .message(
                                event.getMessage()
                        )
                        .build();

        return createNotification(
                request
        );
    }

*/

    // ============================================================
    // PARSE NOTIFICATION TYPE
    // ============================================================

    private NotificationType parseNotificationType(
            String type) {

        if (type == null
                || type.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification type cannot be null or blank"
            );
        }

        try {

            return NotificationType.valueOf(
                    type
            );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid notification type: "
                            + type,
                    exception
            );
        }
    }


    // ============================================================
    // MARK ALL USER NOTIFICATIONS AS READ
    // ============================================================

    @Override
    public void markAllAsRead(
            String userId) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndStatusOrderByCreatedAtDesc(
                                userId,
                                NotificationStatus.UNREAD
                        );

        if (notifications.isEmpty()) {
            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        notifications.forEach(
                notification -> {

                    notification.setStatus(
                            NotificationStatus.READ
                    );

                    notification.setReadAt(
                            now
                    );
                }
        );

        notificationRepository.saveAll(
                notifications
        );
    }


    // ============================================================
    // DELETE NOTIFICATION
    // ============================================================

    @Override
    public void deleteNotification(
            Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + id
                                )
                        );

        notificationRepository.delete(
                notification
        );
    }


    // ============================================================
    // GET UNREAD NOTIFICATION COUNT
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(
            String userId) {

        return notificationRepository
                .countByUserIdAndStatus(
                        userId,
                        NotificationStatus.UNREAD
                );
    }
}