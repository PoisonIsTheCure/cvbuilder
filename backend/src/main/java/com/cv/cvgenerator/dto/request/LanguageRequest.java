package com.cv.cvgenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LanguageRequest {

    @NotBlank(message = "Language name is required")
    private String name;

    private String proficiency;
}
