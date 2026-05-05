package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.*;
import com.cv.cvgenerator.dto.response.*;
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
public class CvEntityService {

    private final MasterCvRepository masterCvRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final LanguageRepository languageRepository;
    private final ProfileRepository profileRepository;

    // ── Education ────────────────────────────────────────────────────────────

    @Transactional
    public EducationResponse addEducation(Long userId, EducationRequest request) {
        MasterCv masterCv = getMasterCv(userId);
        Education education = Education.builder()
                .institution(request.getInstitution())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .current(request.isCurrent())
                .description(request.getDescription())
                .build();
        education = educationRepository.save(education);
        masterCv.getEducations().add(education);
        masterCvRepository.save(masterCv);
        return toEducationResponse(education);
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> getEducations(Long userId) {
        return educationRepository.findAllByUserId(userId)
                .stream().map(this::toEducationResponse).toList();
    }

    @Transactional
    public EducationResponse updateEducation(Long userId, Long educationId, EducationRequest request) {
        Education education = getOwnedEducation(userId, educationId);
        education.setInstitution(request.getInstitution());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setCurrent(request.isCurrent());
        education.setDescription(request.getDescription());
        return toEducationResponse(educationRepository.save(education));
    }

    @Transactional
    public void deleteEducation(Long userId, Long educationId) {
        Education education = getOwnedEducation(userId, educationId);
        MasterCv masterCv = getMasterCv(userId);
        masterCv.getEducations().remove(education);
        masterCvRepository.save(masterCv);
        educationRepository.delete(education);
    }

    // ── Experience ───────────────────────────────────────────────────────────

    @Transactional
    public ExperienceResponse addExperience(Long userId, ExperienceRequest request) {
        MasterCv masterCv = getMasterCv(userId);
        Experience experience = Experience.builder()
                .company(request.getCompany())
                .role(request.getRole())
                .location(request.getLocation())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .current(request.isCurrent())
                .description(request.getDescription())
                .build();
        experience = experienceRepository.save(experience);
        masterCv.getExperiences().add(experience);
        masterCvRepository.save(masterCv);
        return toExperienceResponse(experience);
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponse> getExperiences(Long userId) {
        return experienceRepository.findAllByUserId(userId)
                .stream().map(this::toExperienceResponse).toList();
    }

    @Transactional
    public ExperienceResponse updateExperience(Long userId, Long experienceId, ExperienceRequest request) {
        Experience experience = getOwnedExperience(userId, experienceId);
        experience.setCompany(request.getCompany());
        experience.setRole(request.getRole());
        experience.setLocation(request.getLocation());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setCurrent(request.isCurrent());
        experience.setDescription(request.getDescription());
        return toExperienceResponse(experienceRepository.save(experience));
    }

    @Transactional
    public void deleteExperience(Long userId, Long experienceId) {
        Experience experience = getOwnedExperience(userId, experienceId);
        MasterCv masterCv = getMasterCv(userId);
        masterCv.getExperiences().remove(experience);
        masterCvRepository.save(masterCv);
        experienceRepository.delete(experience);
    }

    // ── Project ──────────────────────────────────────────────────────────────

    @Transactional
    public ProjectResponse addProject(Long userId, ProjectRequest request) {
        MasterCv masterCv = getMasterCv(userId);
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .techStack(request.getTechStack())
                .projectUrl(request.getProjectUrl())
                .repoUrl(request.getRepoUrl())
                .build();
        project = projectRepository.save(project);
        masterCv.getProjects().add(project);
        masterCvRepository.save(masterCv);
        return toProjectResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(Long userId) {
        return projectRepository.findAllByUserId(userId)
                .stream().map(this::toProjectResponse).toList();
    }

    @Transactional
    public ProjectResponse updateProject(Long userId, Long projectId, ProjectRequest request) {
        Project project = getOwnedProject(userId, projectId);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setTechStack(request.getTechStack());
        project.setProjectUrl(request.getProjectUrl());
        project.setRepoUrl(request.getRepoUrl());
        return toProjectResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long userId, Long projectId) {
        Project project = getOwnedProject(userId, projectId);
        MasterCv masterCv = getMasterCv(userId);
        masterCv.getProjects().remove(project);
        masterCvRepository.save(masterCv);
        projectRepository.delete(project);
    }

    // ── Skill ────────────────────────────────────────────────────────────────

    @Transactional
    public SkillResponse addSkill(Long userId, SkillRequest request) {
        // Prevent duplicates
        skillRepository.findByUserIdAndNameIgnoreCase(userId, request.getName())
                .ifPresent(s -> { throw new com.cv.cvgenerator.exception.ConflictException(
                        "Skill already exists: " + request.getName()); });

        MasterCv masterCv = getMasterCv(userId);
        Skill skill = Skill.builder()
                .name(request.getName())
                .category(request.getCategory())
                .level(request.getLevel())
                .build();
        skill = skillRepository.save(skill);
        masterCv.getSkills().add(skill);
        masterCvRepository.save(masterCv);
        return toSkillResponse(skill);
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> getSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream().map(this::toSkillResponse).toList();
    }

    @Transactional
    public SkillResponse updateSkill(Long userId, Long skillId, SkillRequest request) {
        Skill skill = getOwnedSkill(userId, skillId);
        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setLevel(request.getLevel());
        return toSkillResponse(skillRepository.save(skill));
    }

    @Transactional
    public void deleteSkill(Long userId, Long skillId) {
        Skill skill = getOwnedSkill(userId, skillId);
        MasterCv masterCv = getMasterCv(userId);
        masterCv.getSkills().remove(skill);
        masterCvRepository.save(masterCv);
        skillRepository.delete(skill);
    }

    // ── Language ─────────────────────────────────────────────────────────────

    @Transactional
    public LanguageResponse addLanguage(Long userId, LanguageRequest request) {
        MasterCv masterCv = getMasterCv(userId);
        Language language = Language.builder()
                .name(request.getName())
                .proficiency(request.getProficiency())
                .build();
        language = languageRepository.save(language);
        masterCv.getLanguages().add(language);
        masterCvRepository.save(masterCv);
        return toLanguageResponse(language);
    }

    @Transactional(readOnly = true)
    public List<LanguageResponse> getLanguages(Long userId) {
        return languageRepository.findAllByUserId(userId)
                .stream().map(this::toLanguageResponse).toList();
    }

    @Transactional
    public LanguageResponse updateLanguage(Long userId, Long languageId, LanguageRequest request) {
        Language language = getOwnedLanguage(userId, languageId);
        language.setName(request.getName());
        language.setProficiency(request.getProficiency());
        return toLanguageResponse(languageRepository.save(language));
    }

    @Transactional
    public void deleteLanguage(Long userId, Long languageId) {
        Language language = getOwnedLanguage(userId, languageId);
        MasterCv masterCv = getMasterCv(userId);
        masterCv.getLanguages().remove(language);
        masterCvRepository.save(masterCv);
        languageRepository.delete(language);
    }

    // ── Profile ──────────────────────────────────────────────────────────────

    @Transactional
    public ProfileResponse saveProfile(Long userId, ProfileRequest request) {
        MasterCv masterCv = getMasterCv(userId);

        // Upsert profile
        Profile profile = profileRepository.findByMasterCvId(masterCv.getId())
                .orElse(Profile.builder().masterCv(masterCv).build());

        profile.setTitle(request.getTitle());
        profile.setSummary(request.getSummary());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());

        return toProfileResponse(profileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        MasterCv masterCv = getMasterCv(userId);
        Profile profile = profileRepository.findByMasterCvId(masterCv.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return toProfileResponse(profile);
    }

    // ── Ownership guards ─────────────────────────────────────────────────────

    private Education getOwnedEducation(Long userId, Long id) {
        return educationRepository.findAllByUserId(userId).stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Education not accessible: " + id));
    }

    private Experience getOwnedExperience(Long userId, Long id) {
        return experienceRepository.findAllByUserId(userId).stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Experience not accessible: " + id));
    }

    private Project getOwnedProject(Long userId, Long id) {
        return projectRepository.findAllByUserId(userId).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Project not accessible: " + id));
    }

    private Skill getOwnedSkill(Long userId, Long id) {
        return skillRepository.findAllByUserId(userId).stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Skill not accessible: " + id));
    }

    private Language getOwnedLanguage(Long userId, Long id) {
        return languageRepository.findAllByUserId(userId).stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Language not accessible: " + id));
    }

    // ── MasterCv lookup ──────────────────────────────────────────────────────

    private MasterCv getMasterCv(Long userId) {
        return masterCvRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MasterCv not found for user: " + userId));
    }

    // ── Response mappers ─────────────────────────────────────────────────────

    private EducationResponse toEducationResponse(Education e) {
        return EducationResponse.builder()
                .id(e.getId()).institution(e.getInstitution()).degree(e.getDegree())
                .fieldOfStudy(e.getFieldOfStudy()).startDate(e.getStartDate())
                .endDate(e.getEndDate()).current(e.isCurrent()).description(e.getDescription())
                .build();
    }

    private ExperienceResponse toExperienceResponse(Experience e) {
        return ExperienceResponse.builder()
                .id(e.getId()).company(e.getCompany()).role(e.getRole())
                .location(e.getLocation()).startDate(e.getStartDate())
                .endDate(e.getEndDate()).current(e.isCurrent()).description(e.getDescription())
                .build();
    }

    private ProjectResponse toProjectResponse(Project p) {
        return ProjectResponse.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .techStack(p.getTechStack()).projectUrl(p.getProjectUrl()).repoUrl(p.getRepoUrl())
                .build();
    }

    private SkillResponse toSkillResponse(Skill s) {
        return SkillResponse.builder()
                .id(s.getId()).name(s.getName()).category(s.getCategory()).level(s.getLevel())
                .build();
    }

    private LanguageResponse toLanguageResponse(Language l) {
        return LanguageResponse.builder()
                .id(l.getId()).name(l.getName()).proficiency(l.getProficiency())
                .build();
    }

    private ProfileResponse toProfileResponse(Profile p) {
        return ProfileResponse.builder()
                .id(p.getId()).title(p.getTitle()).summary(p.getSummary())
                .phone(p.getPhone()).location(p.getLocation())
                .linkedinUrl(p.getLinkedinUrl()).githubUrl(p.getGithubUrl())
                .portfolioUrl(p.getPortfolioUrl())
                .build();
    }
}
