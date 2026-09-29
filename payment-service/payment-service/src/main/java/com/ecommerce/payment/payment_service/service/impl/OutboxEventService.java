package com.ecommerce.payment.payment_service.service.impl;

import com.ecommerce.payment.payment_service.dto.event.NotificationEvent;
import com.ecommerce.payment.payment_service.entity.OutboxEvent;
import com.ecommerce.payment.payment_service.repository.OutboxEventRepository;

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

    public void saveNotificationEvent(NotificationEvent event) {

        try {

            String payload =
                    objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent =
                    OutboxEvent.builder()
                            .aggregateType("PAYMENT")
                            .aggregateId(
                                    String.valueOf(event.getOrderId())
                            )
                            .eventType(event.getType())
                            .payload(payload)
                            .status("PENDING")
                            .createdAt(LocalDateTime.now())
                            .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize payment notification event",
                    e
            );
        }
    }
}