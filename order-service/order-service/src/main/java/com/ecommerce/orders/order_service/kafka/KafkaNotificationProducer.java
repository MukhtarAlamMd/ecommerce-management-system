
package com.ecommerce.orders.order_service.kafka;

import com.ecommerce.orders.order_service.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationProducer {

   private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Value("${app.kafka.notification-topic}")
    private String notificationTopic;

    public void sendNotification(NotificationEvent event) {

        kafkaTemplate.send(
                notificationTopic,
                event.getUserId(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {

                log.error(
                        "Failed to send notification event for order {}",
                        event.getOrderId(),
                        exception
                );

            } else {

                log.info(
                        "Notification event sent successfully. " +
                        "Topic: {}, Order ID: {}, User ID: {}",
                        notificationTopic,
                        event.getOrderId(),
                        event.getUserId()
                );
            }
        });
    }
}
