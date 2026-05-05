package com.cv.cvgenerator.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAnalysisResult {

    @JsonProperty("match_score")
    private Integer matchScore;

    @JsonProperty("required_skills")
    private List<String> requiredSkills;

    @JsonProperty("missing_skills")
    private List<String> missingSkills;

    @JsonProperty("suggested_project_ids")
    private List<Long> suggestedProjectIds;

    @JsonProperty("suggested_experience_ids")
    private List<Long> suggestedExperienceIds;

    @JsonProperty("summary")
    private String summary;
}