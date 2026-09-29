package com.ecommerce.notification.notification_service.kafka;

import com.ecommerce.notification.notification_service.dto.event.NotificationEvent;
import com.ecommerce.notification.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${app.kafka.notification-topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotification(NotificationEvent event) {

        log.info(
                "Received notification event: type={}, userEmail={}",
                event.getType(),
                event.getRecipient()
        );

        notificationService.createNotificationFromEvent(event);

        log.info(
                "Notification successfully created for user: {}",
                event.getRecipient()
        );
    }
}