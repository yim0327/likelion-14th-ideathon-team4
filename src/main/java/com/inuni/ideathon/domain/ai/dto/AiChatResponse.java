package com.inuni.ideathon.domain.ai.dto;

public record AiChatResponse(
        String reply,
        String riskLevel,
        String suggestedAction
) {
}
