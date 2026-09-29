package com.ecommerce.payment.payment_service.service.impl;

import com.ecommerce.payment.payment_service.dto.event.NotificationEvent;
import com.ecommerce.payment.payment_service.entity.OutboxEvent;
import com.ecommerce.payment.payment_service.repository.OutboxEventRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.notification-topic}")
    private String notificationTopic;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findByStatusOrderByCreatedAtAsc("PENDING");

        if (events.isEmpty()) {
            return;
        }

        log.info(
                "Found {} pending payment outbox event(s)",
                events.size()
        );

        for (OutboxEvent event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEvent outboxEvent) {

        try {

            NotificationEvent notificationEvent =
                    objectMapper.readValue(
                            outboxEvent.getPayload(),
                            NotificationEvent.class
                    );

            log.info(
                    "Publishing payment outbox event: id={}, orderId={}, type={}",
                    outboxEvent.getId(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getEventType()
            );

            kafkaTemplate
                    .send(
                            notificationTopic,
                            outboxEvent.getAggregateId(),
                            notificationEvent
                    )
                    .whenComplete((result, exception) -> {

                        if (exception != null) {

                            log.error(
                                    "Failed to publish payment outbox event: id={}, orderId={}",
                                    outboxEvent.getId(),
                                    outboxEvent.getAggregateId(),
                                    exception
                            );

                            return;
                        }

                        markAsPublished(
                                outboxEvent,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    });

        } catch (Exception e) {

            log.error(
                    "Failed to process payment outbox event: id={}",
                    outboxEvent.getId(),
                    e
            );
        }
    }

    private void markAsPublished(
            OutboxEvent event,
            int partition,
            long offset
    ) {

        event.setStatus("PUBLISHED");
        event.setPublishedAt(LocalDateTime.now());

        outboxEventRepository.save(event);

        log.info(
                "Payment outbox event published successfully: id={}, orderId={}, partition={}, offset={}",
                event.getId(),
                event.getAggregateId(),
                partition,
                offset
        );
    }
}