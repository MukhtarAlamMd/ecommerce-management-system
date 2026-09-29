package com.ecommerce.notification.notification_service.controller;

import com.ecommerce.notification.notification_service.dto.request.CreateNotificationRequest;
import com.ecommerce.notification.notification_service.dto.response.NotificationResponse;
import com.ecommerce.notification.notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private NotificationService notificationService;

    private NotificationResponse response;

    private CreateNotificationRequest request;

    @BeforeEach
    void setUp() {

        NotificationController controller =
                new NotificationController(notificationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        objectMapper = new ObjectMapper();

        response = new NotificationResponse();

        request = mock(CreateNotificationRequest.class);
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void createNotification_ShouldReturn201() throws Exception {

        when(notificationService.createNotification(
                any(CreateNotificationRequest.class)))
                .thenReturn(response);

        /*
         * Using a valid JSON payload is dependent on the exact
         * validation fields in CreateNotificationRequest.
         * This payload matches the fields used by your service.
         */
        String json = """
                {
                    "userId": "user-1",
                    "recipient": "customer@gmail.com",
                    "orderId": 123,
                    "type": "ORDER_CREATED",
                    "title": "Order Created",
                    "message": "Your order has been created"
                }
                """;

        mockMvc.perform(
                post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isCreated());

        verify(notificationService)
                .createNotification(
                        any(CreateNotificationRequest.class)
                );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getNotificationById_ShouldReturn200() throws Exception {

        when(notificationService.getNotificationById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/notifications/1")
        )
        .andExpect(status().isOk());

        verify(notificationService)
                .getNotificationById(1L);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAllNotifications_ShouldReturn200() throws Exception {

        when(notificationService.getAllNotifications())
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/api/notifications")
        )
        .andExpect(status().isOk());

        verify(notificationService)
                .getAllNotifications();
    }

    // =========================================================
    // GET BY USER ID
    // =========================================================

    @Test
    void getNotificationsByUserId_ShouldReturn200()
            throws Exception {

        when(notificationService
                .getNotificationsByUserId("user-1"))
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/api/notifications/user/user-1")
        )
        .andExpect(status().isOk());

        verify(notificationService)
                .getNotificationsByUserId("user-1");
    }

    // =========================================================
    // GET UNREAD
    // =========================================================

    @Test
    void getUnreadNotifications_ShouldReturn200()
            throws Exception {

        when(notificationService
                .getUnreadNotifications("user-1"))
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/api/notifications/user/user-1/unread")
        )
        .andExpect(status().isOk());

        verify(notificationService)
                .getUnreadNotifications("user-1");
    }

    // =========================================================
    // UNREAD COUNT
    // =========================================================

    @Test
    void getUnreadNotificationCount_ShouldReturn200()
            throws Exception {

        when(notificationService
                .getUnreadNotificationCount("user-1"))
                .thenReturn(5L);

        mockMvc.perform(
                get("/api/notifications/user/user-1/unread/count")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("5"));

        verify(notificationService)
                .getUnreadNotificationCount("user-1");
    }

    // =========================================================
    // MARK AS READ
    // =========================================================

    @Test
    void markAsRead_ShouldReturn200() throws Exception {

        when(notificationService.markAsRead(1L))
                .thenReturn(response);

        mockMvc.perform(
                patch("/api/notifications/1/read")
        )
        .andExpect(status().isOk());

        verify(notificationService)
                .markAsRead(1L);
    }

    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @Test
    void markAllAsRead_ShouldReturn204() throws Exception {

        doNothing()
                .when(notificationService)
                .markAllAsRead("user-1");

        mockMvc.perform(
                patch("/api/notifications/user/user-1/read-all")
        )
        .andExpect(status().isNoContent());

        verify(notificationService)
                .markAllAsRead("user-1");
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void deleteNotification_ShouldReturn204() throws Exception {

        doNothing()
                .when(notificationService)
                .deleteNotification(1L);

        mockMvc.perform(
                delete("/api/notifications/1")
        )
        .andExpect(status().isNoContent());

        verify(notificationService)
                .deleteNotification(1L);
    }
}