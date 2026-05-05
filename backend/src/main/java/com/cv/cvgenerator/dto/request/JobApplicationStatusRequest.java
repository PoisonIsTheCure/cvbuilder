package com.cv.cvgenerator.dto.request;

import com.cv.cvgenerator.enums.JobApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private JobApplicationStatus status;
}