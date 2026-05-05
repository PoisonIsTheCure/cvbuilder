package com.cv.cvgenerator.dto.response;

import com.cv.cvgenerator.enums.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {

    private Long id;
    private String role;
    private String company;
    private String jobDescription;
    private String requirements;
    private String jobUrl;
    private JobApplicationStatus status;
    private LocalDate dateApplied;

    // Lightweight references — no full nested objects to keep response lean
    private Long offerAnalysisId;
    private Long tailoredCvId;
}