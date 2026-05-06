package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.TailoredCvRequest;
import com.cv.cvgenerator.dto.response.ExperienceResponse;
import com.cv.cvgenerator.dto.response.ProjectResponse;
import com.cv.cvgenerator.dto.response.SkillResponse;
import com.cv.cvgenerator.dto.response.TailoredCvResponse;
import com.cv.cvgenerator.entity.*;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.exception.UnauthorizedException;
import com.cv.cvgenerator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TailoredCvService {

    private final TailoredCvRepository tailoredCvRepository;
    private final MasterCvRepository masterCvRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final CvLayoutRepository cvLayoutRepository;
    private final UserRepository userRepository;

    // ── Create ───────────────────────────────────────────────────────────────

    @Transactional
    public TailoredCvResponse create(Long userId, TailoredCvRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        MasterCv masterCv = masterCvRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MasterCv not found for user: " + userId));

        JobApplication jobApplication = jobApplicationRepository
                .findByIdAndUserId(request.getJobApplicationId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job application not accessible: " + request.getJobApplicationId()));

        CvLayout cvLayout = request.getCvLayoutId() != null
                ? cvLayoutRepository.findById(request.getCvLayoutId())
                        .orElseThrow(() -> new ResourceNotFoundException("CvLayout", request.getCvLayoutId()))
                : null;

        // Fetch selected subsets — validate they belong to the user
        List<Experience> experiences = request.getExperienceIds() != null
                ? experienceRepository.findByUserIdAndIdIn(userId, request.getExperienceIds())
                : List.of();

        List<Project> projects = request.getProjectIds() != null
                ? projectRepository.findByUserIdAndIdIn(userId, request.getProjectIds())
                : List.of();

        List<Skill> skills = request.getSkillIds() != null
                ? skillRepository.findAllByUserId(userId).stream()
                        .filter(s -> request.getSkillIds().contains(s.getId()))
                        .toList()
                : List.of();

        TailoredCv tailoredCv = TailoredCv.builder()
                .user(user)
                .masterCv(masterCv)
                .jobApplication(jobApplication)
                .cvLayout(cvLayout)
                .experiences(experiences)
                .projects(projects)
                .skills(skills)
                .build();

        return toResponse(tailoredCvRepository.save(tailoredCv));
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TailoredCvResponse> getAllByUser(Long userId) {
        return tailoredCvRepository.findAllByUserId(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TailoredCvResponse getById(Long userId, Long tailoredCvId) {
        TailoredCv tailoredCv = tailoredCvRepository.findByIdWithFullDetails(tailoredCvId)
                .orElseThrow(() -> new ResourceNotFoundException("TailoredCv", tailoredCvId));
        if (!tailoredCv.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("TailoredCv not accessible: " + tailoredCvId);
        }
        return toResponse(tailoredCv);
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long userId, Long tailoredCvId) {
        TailoredCv tailoredCv = tailoredCvRepository.findById(tailoredCvId)
                .orElseThrow(() -> new ResourceNotFoundException("TailoredCv", tailoredCvId));
        if (!tailoredCv.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("TailoredCv not accessible: " + tailoredCvId);
        }
        tailoredCvRepository.delete(tailoredCv);
    }

    // ── Response mapper ──────────────────────────────────────────────────────

    private TailoredCvResponse toResponse(TailoredCv t) {
        return TailoredCvResponse.builder()
                .id(t.getId())
                .jobApplicationId(t.getJobApplication() != null ? t.getJobApplication().getId() : null)
                .cvLayoutId(t.getCvLayout() != null ? t.getCvLayout().getId() : null)
                .generatedPdfPath(t.getGeneratedPdfPath())
                .experiences(t.getExperiences() == null ? List.of() :
                        t.getExperiences().stream().map(e -> ExperienceResponse.builder()
                                .id(e.getId()).company(e.getCompany()).role(e.getRole())
                                .location(e.getLocation()).startDate(e.getStartDate())
                                .endDate(e.getEndDate()).current(e.isCurrent())
                                .description(e.getDescription()).build()).toList())
                .projects(t.getProjects() == null ? List.of() :
                        t.getProjects().stream().map(p -> ProjectResponse.builder()
                                .id(p.getId()).name(p.getName()).description(p.getDescription())
                                .techStack(p.getTechStack()).projectUrl(p.getProjectUrl())
                                .repoUrl(p.getRepoUrl()).build()).toList())
                .skills(t.getSkills() == null ? List.of() :
                        t.getSkills().stream().map(s -> SkillResponse.builder()
                                .id(s.getId()).name(s.getName()).category(s.getCategory())
                                .level(s.getLevel()).build()).toList())
                .build();
    }
}
