package com.cv.cvgenerator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiParamsResponse {

    private Long id;
    private String model;
    private String baseUrl;
    private Integer maxTokens;
    private Double temperature;
    // Note: apiKey is intentionally excluded from responses
}
