package com.ecommerce.payment.payment_service.dto.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String userId;

    private String recipient;

    private Long orderId;

    private String type;

    private String title;

    private String message;
}