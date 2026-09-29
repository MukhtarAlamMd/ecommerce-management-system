package com.ecommerce.orders.order_service.service;

import com.ecommerce.orders.order_service.dto.event.NotificationEvent;
import com.ecommerce.orders.order_service.entity.OutboxEvent;
import com.ecommerce.orders.order_service.repository.OutboxEventRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;


    // =========================================================
    // SAVE NOTIFICATION EVENT
    // =========================================================

    public void saveNotificationEvent(
            NotificationEvent event) {

        try {

            String payload =
                    objectMapper.writeValueAsString(event);


            OutboxEvent outboxEvent =
                    OutboxEvent.builder()
                            .aggregateType("ORDER")
                            .aggregateId(
                                    String.valueOf(
                                            event.getOrderId()
                                    )
                            )
                            .eventType(
                                    event.getType()
                            )
                            .payload(payload)
                            .status("PENDING")
                            .createdAt(
                                    LocalDateTime.now()
                            )
                            .build();


            outboxEventRepository.save(
                    outboxEvent
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize notification event",
                    e
            );
        }
    }
}