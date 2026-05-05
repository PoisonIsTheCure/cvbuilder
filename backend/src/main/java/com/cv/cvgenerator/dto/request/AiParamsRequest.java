package com.cv.cvgenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiParamsRequest {

    @NotBlank(message = "API key is required")
    private String apiKey;

    @NotBlank(message = "Model is required")
    private String model;

    private String baseUrl;

    private Integer maxTokens;

    private Double temperature;
}
