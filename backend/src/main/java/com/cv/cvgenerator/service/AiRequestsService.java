package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.OpenRouterRequest;
import com.cv.cvgenerator.dto.response.OpenRouterResponse;
import com.cv.cvgenerator.entity.AiParams;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.repository.AiParamsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRequestsService {

    private static final String DEFAULT_BASE_URL = "https://openrouter.ai/api/v1";
    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final AiParamsRepository aiParamsRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.referer:https://cvgenerator.app}")
    private String appReferer;

    // ── Main entry point ─────────────────────────────────────────────────────

    public String sendPrompt(Long userId, String systemPrompt, String userPrompt) {
        AiParams aiParams = aiParamsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AI parameters not configured for user: " + userId));

        OpenRouterRequest request = buildRequest(aiParams, systemPrompt, userPrompt);
        return callOpenRouter(aiParams, request);
    }

    // ── Build request ────────────────────────────────────────────────────────

    private OpenRouterRequest buildRequest(AiParams aiParams,
                                            String systemPrompt,
                                            String userPrompt) {
        return OpenRouterRequest.builder()
                .model(aiParams.getModel())
                .maxTokens(aiParams.getMaxTokens() != null ? aiParams.getMaxTokens() : 2000)
                .temperature(aiParams.getTemperature() != null ? aiParams.getTemperature() : 0.3)
                .responseFormat(new OpenRouterRequest.ResponseFormat("json_object"))
                .messages(List.of(
                        new OpenRouterRequest.Message("system", systemPrompt),
                        new OpenRouterRequest.Message("user", userPrompt)
                ))
                .build();
    }

    // ── HTTP call ────────────────────────────────────────────────────────────

    private String callOpenRouter(AiParams aiParams, OpenRouterRequest request) {
        String baseUrl = aiParams.getBaseUrl() != null
                ? aiParams.getBaseUrl()
                : DEFAULT_BASE_URL;

        try {
            OpenRouterResponse response = webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .post()
                    .uri(CHAT_COMPLETIONS_PATH)
                    .header("Authorization", "Bearer " + aiParams.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("HTTP-Referer", appReferer)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenRouterResponse.class)
                    .block();

            if (response == null || response.getFirstChoiceContent() == null) {
                throw new RuntimeException("Empty response received from AI provider");
            }

            log.debug("AI response received. Tokens used: {}",
                    response.getUsage() != null ? response.getUsage().getTotalTokens() : "unknown");

            return response.getFirstChoiceContent();

        } catch (WebClientResponseException e) {
            log.error("OpenRouter API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI provider error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Unexpected error calling AI provider: {}", e.getMessage());
            throw new RuntimeException("Failed to get AI response: " + e.getMessage());
        }
    }
}