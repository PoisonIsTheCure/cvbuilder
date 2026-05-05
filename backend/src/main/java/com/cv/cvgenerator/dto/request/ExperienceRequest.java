package com.cv.cvgenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceRequest {

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Role is required")
    private String role;

    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    private String description;
}
