package com.ecommerce.ai_service.dto;

public record MessageClassificationResponse(
        String category,
        String priority,
        double confidence
) {
}