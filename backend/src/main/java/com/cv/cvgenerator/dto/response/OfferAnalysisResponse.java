package com.cv.cvgenerator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferAnalysisResponse {

    private Long id;
    private Long jobApplicationId;
    private Integer matchScore;
    private List<String> requiredSkills;
    private List<String> missingSkills;
    private List<Long> suggestedProjectIds;
    private List<Long> suggestedExperienceIds;
    private String aiSummary;
}