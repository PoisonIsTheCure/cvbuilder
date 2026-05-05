package com.cv.cvgenerator.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterRequest {

    private String model;

    private List<Message> messages;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    private Double temperature;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    // ── Nested types ─────────────────────────────────────────────────────────

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;    // "system" | "user" | "assistant"
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseFormat {
        private String type;    // "json_object" to force JSON output from the model
    }
}