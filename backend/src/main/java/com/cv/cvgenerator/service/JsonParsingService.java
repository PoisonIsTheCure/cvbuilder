package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.CvImportRequest;
import com.cv.cvgenerator.dto.response.MasterCvResponse;
import com.cv.cvgenerator.entity.*;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonParsingService {

    private final MasterCvRepository masterCvRepository;
    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final LanguageRepository languageRepository;
    private final MasterCvService masterCvService;
    private final ObjectMapper objectMapper;

    // ── Import from file upload ───────────────────────────────────────────────

    @Transactional
    public MasterCvResponse importFromFile(Long userId, MultipartFile file) {
        CvImportRequest importRequest = parseFile(file);
        return importFromRequest(userId, importRequest);
    }

    // ── Export to JSON ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] exportToJson(Long userId) {
        MasterCv masterCv = masterCvRepository.findByUserIdWithFullDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MasterCv not found for user: " + userId));

        Profile profile = profileRepository.findByMasterCvId(masterCv.getId()).orElse(null);

        // Build export object mirroring CvImportRequest structure
        // so the export can be directly re-imported
        CvImportRequest export = new CvImportRequest();

        if (profile != null) {
            CvImportRequest.ProfileData profileData = new CvImportRequest.ProfileData();
            profileData.setTitle(profile.getTitle());
            profileData.setSummary(profile.getSummary());
            profileData.setPhone(profile.getPhone());
            profileData.setLocation(profile.getLocation());
            profileData.setLinkedinUrl(profile.getLinkedinUrl());
            profileData.setGithubUrl(profile.getGithubUrl());
            profileData.setPortfolioUrl(profile.getPortfolioUrl());
            export.setProfile(profileData);
        }

        if (masterCv.getEducations() != null) {
            export.setEducations(masterCv.getEducations().stream().map(e -> {
                CvImportRequest.EducationData d = new CvImportRequest.EducationData();
                d.setInstitution(e.getInstitution());
                d.setDegree(e.getDegree());
                d.setFieldOfStudy(e.getFieldOfStudy());
                d.setStartDate(e.getStartDate());
                d.setEndDate(e.getEndDate());
                d.setCurrent(e.isCurrent());
                d.setDescription(e.getDescription());
                return d;
            }).toList());
        }

        if (masterCv.getExperiences() != null) {
            export.setExperiences(masterCv.getExperiences().stream().map(e -> {
                CvImportRequest.ExperienceData d = new CvImportRequest.ExperienceData();
                d.setCompany(e.getCompany());
                d.setRole(e.getRole());
                d.setLocation(e.getLocation());
                d.setStartDate(e.getStartDate());
                d.setEndDate(e.getEndDate());
                d.setCurrent(e.isCurrent());
                d.setDescription(e.getDescription());
                return d;
            }).toList());
        }

        if (masterCv.getProjects() != null) {
            export.setProjects(masterCv.getProjects().stream().map(p -> {
                CvImportRequest.ProjectData d = new CvImportRequest.ProjectData();
                d.setName(p.getName());
                d.setDescription(p.getDescription());
                d.setTechStack(p.getTechStack());
                d.setProjectUrl(p.getProjectUrl());
                d.setRepoUrl(p.getRepoUrl());
                return d;
            }).toList());
        }

        if (masterCv.getSkills() != null) {
            export.setSkills(masterCv.getSkills().stream().map(s -> {
                CvImportRequest.SkillData d = new CvImportRequest.SkillData();
                d.setName(s.getName());
                d.setCategory(s.getCategory());
                d.setLevel(s.getLevel());
                return d;
            }).toList());
        }

        if (masterCv.getLanguages() != null) {
            export.setLanguages(masterCv.getLanguages().stream().map(l -> {
                CvImportRequest.LanguageData d = new CvImportRequest.LanguageData();
                d.setName(l.getName());
                d.setProficiency(l.getProficiency());
                return d;
            }).toList());
        }

        try {
            return objectMapper.writer()
                    .with(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsBytes(export);
        } catch (IOException e) {
            log.error("Failed to serialize CV export: {}", e.getMessage());
            throw new RuntimeException("Failed to export CV as JSON: " + e.getMessage());
        }
    }

    // ── Import from parsed request ────────────────────────────────────────────

    @Transactional
    public MasterCvResponse importFromRequest(Long userId, CvImportRequest request) {
        MasterCv masterCv = masterCvRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MasterCv not found for user: " + userId));

        // Import each section — additive by default, skips duplicates
        if (request.getProfile() != null) {
            importProfile(masterCv, request.getProfile());
        }
        if (request.getEducations() != null) {
            importEducations(masterCv, request.getEducations());
        }
        if (request.getExperiences() != null) {
            importExperiences(masterCv, request.getExperiences());
        }
        if (request.getProjects() != null) {
            importProjects(masterCv, request.getProjects());
        }
        if (request.getSkills() != null) {
            importSkills(masterCv, request.getSkills());
        }
        if (request.getLanguages() != null) {
            importLanguages(masterCv, request.getLanguages());
        }

        masterCvRepository.save(masterCv);

        log.info("CV import completed for user: {}", userId);
        return masterCvService.getMasterCv(userId);
    }

    // ── Section importers ─────────────────────────────────────────────────────

    private void importProfile(MasterCv masterCv, CvImportRequest.ProfileData data) {
        // Upsert profile
        Profile profile = profileRepository.findByMasterCvId(masterCv.getId())
                .orElse(Profile.builder().masterCv(masterCv).build());

        if (data.getTitle() != null)        profile.setTitle(data.getTitle());
        if (data.getSummary() != null)      profile.setSummary(data.getSummary());
        if (data.getPhone() != null)        profile.setPhone(data.getPhone());
        if (data.getLocation() != null)     profile.setLocation(data.getLocation());
        if (data.getLinkedinUrl() != null)  profile.setLinkedinUrl(data.getLinkedinUrl());
        if (data.getGithubUrl() != null)    profile.setGithubUrl(data.getGithubUrl());
        if (data.getPortfolioUrl() != null) profile.setPortfolioUrl(data.getPortfolioUrl());

        profileRepository.save(profile);
    }

    private void importEducations(MasterCv masterCv, List<CvImportRequest.EducationData> dataList) {
        if (masterCv.getEducations() == null) masterCv.setEducations(new ArrayList<>());

        for (CvImportRequest.EducationData data : dataList) {
            if (data.getInstitution() == null || data.getDegree() == null) {
                log.warn("Skipping education entry — institution or degree is missing");
                continue;
            }

            // Skip duplicates — same institution + degree
            boolean exists = masterCv.getEducations().stream()
                    .anyMatch(e -> e.getInstitution().equalsIgnoreCase(data.getInstitution())
                            && e.getDegree().equalsIgnoreCase(data.getDegree()));
            if (exists) {
                log.debug("Skipping duplicate education: {} at {}", data.getDegree(), data.getInstitution());
                continue;
            }

            Education education = Education.builder()
                    .institution(data.getInstitution())
                    .degree(data.getDegree())
                    .fieldOfStudy(data.getFieldOfStudy())
                    .startDate(data.getStartDate())
                    .endDate(data.getEndDate())
                    .current(data.getCurrent())
                    .description(data.getDescription())
                    .build();

            masterCv.getEducations().add(educationRepository.save(education));
        }
    }

    private void importExperiences(MasterCv masterCv, List<CvImportRequest.ExperienceData> dataList) {
        if (masterCv.getExperiences() == null) masterCv.setExperiences(new ArrayList<>());

        for (CvImportRequest.ExperienceData data : dataList) {
            if (data.getCompany() == null || data.getRole() == null) {
                log.warn("Skipping experience entry — company or role is missing");
                continue;
            }

            // Skip duplicates — same company + role + startDate
            boolean exists = masterCv.getExperiences().stream()
                    .anyMatch(e -> e.getCompany().equalsIgnoreCase(data.getCompany())
                            && e.getRole().equalsIgnoreCase(data.getRole())
                            && java.util.Objects.equals(e.getStartDate(), data.getStartDate()));
            if (exists) {
                log.debug("Skipping duplicate experience: {} at {}", data.getRole(), data.getCompany());
                continue;
            }

            Experience experience = Experience.builder()
                    .company(data.getCompany())
                    .role(data.getRole())
                    .location(data.getLocation())
                    .startDate(data.getStartDate())
                    .endDate(data.getEndDate())
                    .current(data.getCurrent())
                    .description(data.getDescription())
                    .build();

            masterCv.getExperiences().add(experienceRepository.save(experience));
        }
    }

    private void importProjects(MasterCv masterCv, List<CvImportRequest.ProjectData> dataList) {
        if (masterCv.getProjects() == null) masterCv.setProjects(new ArrayList<>());

        for (CvImportRequest.ProjectData data : dataList) {
            if (data.getName() == null) {
                log.warn("Skipping project entry — name is missing");
                continue;
            }

            // Skip duplicates — same project name
            boolean exists = masterCv.getProjects().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(data.getName()));
            if (exists) {
                log.debug("Skipping duplicate project: {}", data.getName());
                continue;
            }

            Project project = Project.builder()
                    .name(data.getName())
                    .description(data.getDescription())
                    .techStack(data.getTechStack())
                    .projectUrl(data.getProjectUrl())
                    .repoUrl(data.getRepoUrl())
                    .build();

            masterCv.getProjects().add(projectRepository.save(project));
        }
    }

    private void importSkills(MasterCv masterCv, List<CvImportRequest.SkillData> dataList) {
        if (masterCv.getSkills() == null) masterCv.setSkills(new ArrayList<>());

        for (CvImportRequest.SkillData data : dataList) {
            if (data.getName() == null) {
                log.warn("Skipping skill entry — name is missing");
                continue;
            }

            // Skip duplicates — case insensitive name match
            boolean exists = masterCv.getSkills().stream()
                    .anyMatch(s -> s.getName().equalsIgnoreCase(data.getName()));
            if (exists) {
                log.debug("Skipping duplicate skill: {}", data.getName());
                continue;
            }

            Skill skill = Skill.builder()
                    .name(data.getName())
                    .category(data.getCategory())
                    .level(data.getLevel())
                    .build();

            masterCv.getSkills().add(skillRepository.save(skill));
        }
    }

    private void importLanguages(MasterCv masterCv, List<CvImportRequest.LanguageData> dataList) {
        if (masterCv.getLanguages() == null) masterCv.setLanguages(new ArrayList<>());

        for (CvImportRequest.LanguageData data : dataList) {
            if (data.getName() == null) {
                log.warn("Skipping language entry — name is missing");
                continue;
            }

            // Skip duplicates
            boolean exists = masterCv.getLanguages().stream()
                    .anyMatch(l -> l.getName().equalsIgnoreCase(data.getName()));
            if (exists) {
                log.debug("Skipping duplicate language: {}", data.getName());
                continue;
            }

            Language language = Language.builder()
                    .name(data.getName())
                    .proficiency(data.getProficiency())
                    .build();

            masterCv.getLanguages().add(languageRepository.save(language));
        }
    }

    // ── File parser ───────────────────────────────────────────────────────────

    private CvImportRequest parseFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Import file is empty or missing");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".json")) {
            throw new IllegalArgumentException("Only JSON files are supported for import");
        }

        try {
            return objectMapper.readValue(file.getInputStream(), CvImportRequest.class);
        } catch (IOException e) {
            log.error("Failed to parse CV import file: {}", e.getMessage());
            throw new RuntimeException("Failed to parse import file — ensure it is valid JSON: " + e.getMessage());
        }
    }
}