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
public class TailoredCvResponse {

    private Long id;
    private Long jobApplicationId;
    private Long cvLayoutId;
    private List<ExperienceResponse> experiences;
    private List<ProjectResponse> projects;
    private List<SkillResponse> skills;
    private String generatedPdfPath;
}
