package com.ecommerce.ai_service.controller;

import com.ecommerce.ai_service.dto.CustomerMessageRequest;
import com.ecommerce.ai_service.dto.MessageClassificationResponse;
import com.ecommerce.ai_service.service.AiClassificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class CustomerMessageController {

    private final AiClassificationService aiClassificationService;

    public CustomerMessageController(
            AiClassificationService aiClassificationService) {
        this.aiClassificationService = aiClassificationService;
    }

    @PostMapping("/classify")
    public MessageClassificationResponse classify(
            @RequestBody CustomerMessageRequest request) {

        return aiClassificationService.classify(request.message());
    }
}

