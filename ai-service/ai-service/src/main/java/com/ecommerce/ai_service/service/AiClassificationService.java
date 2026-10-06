package com.ecommerce.ai_service.service;
import com.ecommerce.ai_service.dto.MessageClassificationResponse;
import org.springframework.stereotype.Service;

@Service
public class AiClassificationService {

    public MessageClassificationResponse classify(String message) {

        String text = message.toLowerCase();

        if (text.contains("payment")
                || text.contains("paid")
                || text.contains("deducted")
                || text.contains("transaction")) {

            return new MessageClassificationResponse(
                    "PAYMENT_ISSUE",
                    "HIGH",
                    0.94
            );
        }

        if (text.contains("where is my order")
                || text.contains("order status")
                || text.contains("pending order")) {

            return new MessageClassificationResponse(
                    "ORDER_STATUS",
                    "MEDIUM",
                    0.91
            );
        }

        if (text.contains("delivery")
                || text.contains("shipping")
                || text.contains("courier")
                || text.contains("delayed")) {

            return new MessageClassificationResponse(
                    "DELIVERY_STATUS",
                    "HIGH",
                    0.92
            );
        }

        if (text.contains("return")
                || text.contains("send back")) {

            return new MessageClassificationResponse(
                    "RETURN_REQUEST",
                    "MEDIUM",
                    0.90
            );
        }

        if (text.contains("refund")
                || text.contains("money back")) {

            return new MessageClassificationResponse(
                    "REFUND_REQUEST",
                    "HIGH",
                    0.93
            );
        }

        if (text.contains("price")
                || text.contains("size")
                || text.contains("color")
                || text.contains("available")) {

            return new MessageClassificationResponse(
                    "PRODUCT_QUESTION",
                    "LOW",
                    0.88
            );
        }

        if (text.contains("bad")
                || text.contains("worst")
                || text.contains("angry")
                || text.contains("complaint")) {

            return new MessageClassificationResponse(
                    "COMPLAINT",
                    "HIGH",
                    0.89
            );
        }

        return new MessageClassificationResponse(
                "GENERAL",
                "LOW",
                0.75
        );
    }
}

