package com.ecommerce.orders.order_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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