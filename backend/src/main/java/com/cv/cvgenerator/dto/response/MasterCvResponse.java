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
public class MasterCvResponse {
    private Long id;
    private ProfileResponse profile;
    private List<EducationResponse> educations;
    private List<ExperienceResponse> experiences;
    private List<ProjectResponse> projects;
    private List<SkillResponse> skills;
    private List<LanguageResponse> languages;
}
