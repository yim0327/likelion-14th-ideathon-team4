package com.inuni.ideathon.domain.ai.controller;

import com.inuni.ideathon.domain.ai.dto.AiChatRequest;
import com.inuni.ideathon.domain.ai.dto.AiChatResponse;
import com.inuni.ideathon.domain.ai.service.AiChatService;
import com.inuni.ideathon.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(
            @Valid @RequestBody AiChatRequest request
    ) {
        AiChatResponse response = aiChatService.chat(request.message());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
