package com.inuni.ideathon.domain.ai.service;

import com.inuni.ideathon.domain.ai.dto.AiChatResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private final RestClient openAiRestClient;
    private final JsonMapper jsonMapper;

    @Value("${openai.model}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            너는 '혼자ON'이라는 AI 안심귀가 동행자다.

            사용자는 혼자 귀가하거나 혼자 있어 불안을 느끼는 상황이다.

            너의 역할:
            - 사용자의 불안을 치료하거나 진단하지 않는다.
            - 경찰, 보호자, 의료 전문가를 대신한다고 말하지 않는다.
            - 답변은 짧고 차분하게 한다.
            - 사용자가 주변을 계속 인식할 수 있도록 2~3문장 이내로 답한다.
            - 사용자가 위험 표현을 하면 밝은 곳, 사람 많은 곳, 편의점, 큰길 쪽으로 이동하도록 안내한다.
            - "누가 따라와", "무서워", "도와줘", "위험해", "살려줘" 같은 표현이 나오면 riskLevel을 DANGER로 판단한다.
            - "골목길", "불안해", "좀 무서워", "혼자야" 같은 표현은 CAUTION으로 판단한다.
            - 일반적인 대화는 NORMAL로 판단한다.
            - 위험하다고 판단되면 보호자 위치 공유 또는 긴급 버튼 사용을 안내한다.
            - 자동 신고한다고 말하지 않는다. 사용자가 직접 긴급 버튼을 누르거나 보호자에게 공유할 수 있다고 안내한다.

            응답은 반드시 아래 JSON 형식만 반환한다.
            마크다운 코드블록을 쓰지 마라.
            설명 문장을 JSON 밖에 쓰지 마라.

            {
              "reply": "사용자에게 보여줄 답변",
              "riskLevel": "NORMAL | CAUTION | DANGER",
              "suggestedAction": "KEEP_TALKING | CHECK_IN | SHARE_LOCATION | EMERGENCY_BUTTON"
            }
            """;

    public AiChatResponse chat(String userMessage) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "instructions", SYSTEM_PROMPT,
                    "input", userMessage,
                    "max_output_tokens", 300
            );

            JsonNode openAiResponse = openAiRestClient.post()
                    .uri("/responses")
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            String outputText = extractOutputText(openAiResponse);
            String cleanedJson = cleanJson(outputText);

            return jsonMapper.readValue(cleanedJson, AiChatResponse.class);

        } catch (RestClientResponseException e) {
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 처리 실패: " + e.getMessage(), e);
        }
    }

    private String extractOutputText(JsonNode root) {
        if (root == null) {
            throw new IllegalStateException("OpenAI 응답이 비어 있습니다.");
        }

        if (root.has("output_text")) {
            return root.get("output_text").asText();
        }

        JsonNode outputArray = root.path("output");

        for (JsonNode output : outputArray) {
            JsonNode contentArray = output.path("content");

            for (JsonNode content : contentArray) {
                if ("output_text".equals(content.path("type").asText())) {
                    return content.path("text").asText();
                }
            }
        }

        throw new IllegalStateException("OpenAI 응답에서 output_text를 찾을 수 없습니다: " + root);
    }

    private String cleanJson(String text) {
        return text
                .replaceAll("^```json\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("\\s*```$", "")
                .trim();
    }
}
