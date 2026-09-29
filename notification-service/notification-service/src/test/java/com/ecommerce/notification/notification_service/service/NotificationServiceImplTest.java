package com.ecommerce.notification.notification_service.service;

import com.ecommerce.notification.notification_service.dto.event.NotificationEvent;
import com.ecommerce.notification.notification_service.dto.request.CreateNotificationRequest;
import com.ecommerce.notification.notification_service.dto.response.NotificationResponse;
import com.ecommerce.notification.notification_service.entity.Notification;
import com.ecommerce.notification.notification_service.enums.NotificationStatus;
import com.ecommerce.notification.notification_service.enums.NotificationType;
import com.ecommerce.notification.notification_service.exception.ResourceNotFoundException;
import com.ecommerce.notification.notification_service.mapper.NotificationMapper;
import com.ecommerce.notification.notification_service.repository.NotificationRepository;
import com.ecommerce.notification.notification_service.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationResponse response;

    @BeforeEach
    void setUp() {

        notification = new Notification();

        notification.setId(1L);
        notification.setUserId("user-1");
        notification.setRecipient("customer@gmail.com");
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setTitle("Order Created");
        notification.setMessage("Your order has been created");

        response = new NotificationResponse();
    }

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    @Test
    void createNotification_ShouldCreateSuccessfully() {

        CreateNotificationRequest request =
                mock(CreateNotificationRequest.class);

        when(notificationMapper.toEntity(request))
                .thenReturn(notification);

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        NotificationResponse result =
                notificationService.createNotification(request);

        assertNotNull(result);
        assertSame(response, result);

        verify(notificationMapper)
                .toEntity(request);

        verify(notificationRepository)
                .save(notification);

        verify(notificationMapper)
                .toResponse(notification);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getNotificationById_ShouldReturnNotification() {

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        NotificationResponse result =
                notificationService.getNotificationById(1L);

        assertNotNull(result);
        assertSame(response, result);

        verify(notificationRepository)
                .findById(1L);

        verify(notificationMapper)
                .toResponse(notification);
    }

    // =========================================================
    // GET BY ID - NOT FOUND
    // =========================================================

    @Test
    void getNotificationById_ShouldThrowExceptionWhenNotFound() {

        when(notificationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.getNotificationById(99L)
        );

        verify(notificationRepository)
                .findById(99L);

        verify(notificationMapper, never())
                .toResponse(any(Notification.class));
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAllNotifications_ShouldReturnAllNotifications() {

        Notification second = new Notification();
        second.setId(2L);

        NotificationResponse secondResponse =
                new NotificationResponse();

        when(notificationRepository.findAll())
                .thenReturn(List.of(notification, second));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        when(notificationMapper.toResponse(second))
                .thenReturn(secondResponse);

        List<NotificationResponse> result =
                notificationService.getAllNotifications();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(response, result.get(0));
        assertSame(secondResponse, result.get(1));

        verify(notificationRepository)
                .findAll();

        verify(notificationMapper)
                .toResponse(notification);

        verify(notificationMapper)
                .toResponse(second);
    }

    // =========================================================
    // GET ALL - EMPTY
    // =========================================================

    @Test
    void getAllNotifications_ShouldReturnEmptyList() {

        when(notificationRepository.findAll())
                .thenReturn(List.of());

        List<NotificationResponse> result =
                notificationService.getAllNotifications();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(notificationRepository)
                .findAll();

        verifyNoInteractions(notificationMapper);
    }

    // =========================================================
    // GET BY EMAIL
    // =========================================================

    @Test
    void getUserNotifications_ShouldReturnNotifications() {

        when(notificationRepository
                .findByRecipientOrderByCreatedAtDesc(
                        "customer@gmail.com"))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        List<NotificationResponse> result =
                notificationService.getUserNotifications(
                        "customer@gmail.com"
                );

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(notificationRepository)
                .findByRecipientOrderByCreatedAtDesc(
                        "customer@gmail.com"
                );

        verify(notificationMapper)
                .toResponse(notification);
    }

    // =========================================================
    // GET BY USER ID
    // =========================================================

    @Test
    void getNotificationsByUserId_ShouldReturnNotifications() {

        when(notificationRepository
                .findByUserIdOrderByCreatedAtDesc("user-1"))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        List<NotificationResponse> result =
                notificationService.getNotificationsByUserId("user-1");

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(notificationRepository)
                .findByUserIdOrderByCreatedAtDesc("user-1");
    }

    // =========================================================
    // GET UNREAD
    // =========================================================

    @Test
    void getUnreadNotifications_ShouldReturnUnreadNotifications() {

        when(notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        "user-1",
                        NotificationStatus.UNREAD))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        List<NotificationResponse> result =
                notificationService.getUnreadNotifications("user-1");

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(notificationRepository)
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        "user-1",
                        NotificationStatus.UNREAD
                );
    }

    // =========================================================
    // MARK AS READ
    // =========================================================

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        NotificationResponse result =
                notificationService.markAsRead(1L);

        assertNotNull(result);
        assertEquals(
                NotificationStatus.READ,
                notification.getStatus()
        );
        assertNotNull(notification.getReadAt());

        verify(notificationRepository)
                .findById(1L);

        verify(notificationRepository)
                .save(notification);

        verify(notificationMapper)
                .toResponse(notification);
    }

    // =========================================================
    // MARK AS READ - NOT FOUND
    // =========================================================

    @Test
    void markAsRead_ShouldThrowExceptionWhenNotFound() {

        when(notificationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAsRead(99L)
        );

        verify(notificationRepository)
                .findById(99L);

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    // =========================================================
    // CREATE FROM KAFKA EVENT
    // =========================================================

    @Test
    void createNotificationFromEvent_ShouldCreateNotification() {

        NotificationEvent event =
                mock(NotificationEvent.class);

        when(event.getUserId())
                .thenReturn("user-1");

        when(event.getRecipient())
                .thenReturn("123");

        when(event.getType())
                .thenReturn(NotificationType.ORDER_CREATED.name());

        when(event.getTitle())
                .thenReturn("Order Created");

        when(event.getMessage())
                .thenReturn("Order created successfully");

        NotificationResponse expectedResponse =
                new NotificationResponse();

        /*
         * createNotificationFromEvent() delegates to
         * createNotification(), so mock that internal flow
         * through the repository/mapper.
         */
        when(notificationMapper.toEntity(any(
                CreateNotificationRequest.class)))
                .thenReturn(notification);

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        when(notificationMapper.toResponse(notification))
                .thenReturn(expectedResponse);

        NotificationResponse result =
                notificationService
                        .createNotificationFromEvent(event);

        assertNotNull(result);
        assertSame(expectedResponse, result);

        verify(notificationMapper)
                .toEntity(any(CreateNotificationRequest.class));

        verify(notificationRepository)
                .save(notification);

        verify(notificationMapper)
                .toResponse(notification);
    }

    // =========================================================
    // INVALID NOTIFICATION TYPE
    // =========================================================

    @Test
    void createNotificationFromEvent_ShouldThrowExceptionForInvalidType() {

        NotificationEvent event =
                mock(NotificationEvent.class);

        when(event.getType())
                .thenReturn("INVALID_TYPE");

        assertThrows(
                IllegalArgumentException.class,
                () -> notificationService
                        .createNotificationFromEvent(event)
        );

        verifyNoInteractions(notificationRepository);

        verifyNoInteractions(notificationMapper);
    }
    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @Test
    void markAllAsRead_ShouldMarkAllUnreadNotificationsAsRead() {

        Notification second = new Notification();
        second.setId(2L);
        second.setStatus(NotificationStatus.UNREAD);

        notification.setStatus(NotificationStatus.UNREAD);

        when(notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        "user-1",
                        NotificationStatus.UNREAD))
                .thenReturn(List.of(notification, second));

        notificationService.markAllAsRead("user-1");

        assertEquals(
                NotificationStatus.READ,
                notification.getStatus()
        );

        assertEquals(
                NotificationStatus.READ,
                second.getStatus()
        );

        assertNotNull(notification.getReadAt());
        assertNotNull(second.getReadAt());

        verify(notificationRepository)
                .saveAll(List.of(notification, second));
    }

    // =========================================================
    // MARK ALL AS READ - NOTHING TO UPDATE
    // =========================================================

    @Test
    void markAllAsRead_ShouldDoNothingWhenNoUnreadNotifications() {

        when(notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        "user-1",
                        NotificationStatus.UNREAD))
                .thenReturn(List.of());

        notificationService.markAllAsRead("user-1");

        verify(notificationRepository)
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        "user-1",
                        NotificationStatus.UNREAD
                );

        verify(notificationRepository, never())
                .saveAll(anyList());
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void deleteNotification_ShouldDeleteSuccessfully() {

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        notificationService.deleteNotification(1L);

        verify(notificationRepository)
                .findById(1L);

        verify(notificationRepository)
                .delete(notification);
    }

    // =========================================================
    // DELETE - NOT FOUND
    // =========================================================

    @Test
    void deleteNotification_ShouldThrowExceptionWhenNotFound() {

        when(notificationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.deleteNotification(99L)
        );

        verify(notificationRepository)
                .findById(99L);

        verify(notificationRepository, never())
                .delete(any(Notification.class));
    }

    // =========================================================
    // UNREAD COUNT
    // =========================================================

    @Test
    void getUnreadNotificationCount_ShouldReturnCount() {

        when(notificationRepository.countByUserIdAndStatus(
                "user-1",
                NotificationStatus.UNREAD
        )).thenReturn(5L);

        long result =
                notificationService
                        .getUnreadNotificationCount("user-1");

        assertEquals(5L, result);

        verify(notificationRepository)
                .countByUserIdAndStatus(
                        "user-1",
                        NotificationStatus.UNREAD
                );
    }

    // =========================================================
    // UNREAD COUNT - ZERO
    // =========================================================

    @Test
    void getUnreadNotificationCount_ShouldReturnZero() {

        when(notificationRepository.countByUserIdAndStatus(
                "user-1",
                NotificationStatus.UNREAD
        )).thenReturn(0L);

        long result =
                notificationService
                        .getUnreadNotificationCount("user-1");

        assertEquals(0L, result);
    }
}