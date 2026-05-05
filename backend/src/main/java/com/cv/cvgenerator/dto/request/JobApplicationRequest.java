package com.cv.cvgenerator.dto.request;

import com.cv.cvgenerator.enums.JobApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationRequest {

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Company is required")
    private String company;

    private String jobDescription;

    private String requirements;

    private String jobUrl;

    @NotNull(message = "Status is required")
    private JobApplicationStatus status;

    private LocalDate dateApplied;
}