package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.response.*;
import com.cv.cvgenerator.entity.MasterCv;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.repository.MasterCvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MasterCvService {

    private final MasterCvRepository masterCvRepository;

    // ── Get full MasterCv ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MasterCvResponse getMasterCv(Long userId) {
        MasterCv masterCv = masterCvRepository.findByUserIdWithFullDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MasterCv not found for user: " + userId));
        return toResponse(masterCv);
    }

    // ── Internal helper (used by other services) ─────────────────────────────

    public MasterCv getMasterCvEntity(Long userId) {
        return masterCvRepository.findByUserIdWithFullDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MasterCv not found for user: " + userId));
    }

    // ── Response mapper ──────────────────────────────────────────────────────

    private MasterCvResponse toResponse(MasterCv m) {
        return MasterCvResponse.builder()
                .id(m.getId())
                .profile(m.getProfile() != null ? ProfileResponse.builder()
                        .id(m.getProfile().getId())
                        .title(m.getProfile().getTitle())
                        .summary(m.getProfile().getSummary())
                        .phone(m.getProfile().getPhone())
                        .location(m.getProfile().getLocation())
                        .linkedinUrl(m.getProfile().getLinkedinUrl())
                        .githubUrl(m.getProfile().getGithubUrl())
                        .portfolioUrl(m.getProfile().getPortfolioUrl())
                        .build() : null)
                .educations(m.getEducations() == null ? java.util.List.of() :
                        m.getEducations().stream().map(e -> EducationResponse.builder()
                                .id(e.getId()).institution(e.getInstitution()).degree(e.getDegree())
                                .fieldOfStudy(e.getFieldOfStudy()).startDate(e.getStartDate())
                                .endDate(e.getEndDate()).current(e.isCurrent()).description(e.getDescription())
                                .build()).toList())
                .experiences(m.getExperiences() == null ? java.util.List.of() :
                        m.getExperiences().stream().map(e -> ExperienceResponse.builder()
                                .id(e.getId()).company(e.getCompany()).role(e.getRole())
                                .location(e.getLocation()).startDate(e.getStartDate())
                                .endDate(e.getEndDate()).current(e.isCurrent()).description(e.getDescription())
                                .build()).toList())
                .projects(m.getProjects() == null ? java.util.List.of() :
                        m.getProjects().stream().map(p -> ProjectResponse.builder()
                                .id(p.getId()).name(p.getName()).description(p.getDescription())
                                .techStack(p.getTechStack()).projectUrl(p.getProjectUrl()).repoUrl(p.getRepoUrl())
                                .build()).toList())
                .skills(m.getSkills() == null ? java.util.List.of() :
                        m.getSkills().stream().map(s -> SkillResponse.builder()
                                .id(s.getId()).name(s.getName()).category(s.getCategory()).level(s.getLevel())
                                .build()).toList())
                .languages(m.getLanguages() == null ? java.util.List.of() :
                        m.getLanguages().stream().map(l -> LanguageResponse.builder()
                                .id(l.getId()).name(l.getName()).proficiency(l.getProficiency())
                                .build()).toList())
                .build();
    }
}
