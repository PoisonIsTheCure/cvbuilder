package com.cv.cvgenerator.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TailoredCvRequest {

    @NotNull(message = "Job application ID is required")
    private Long jobApplicationId;

    private Long cvLayoutId;

    private List<Long> experienceIds;

    private List<Long> projectIds;

    private List<Long> skillIds;
}
