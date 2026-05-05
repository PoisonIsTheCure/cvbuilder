package com.cv.cvgenerator.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Models the JSON structure accepted for CV import.
 * Designed to be flexible — all fields are optional so partial imports work.
 * Compatible with a normalized LinkedIn-style export or custom JSON.
 *
 * Example structure:
 * {
 *   "profile": { "title": "...", "summary": "...", "phone": "...", ... },
 *   "experiences": [ { "company": "...", "role": "...", ... } ],
 *   "educations": [ { "institution": "...", "degree": "...", ... } ],
 *   "projects": [ { "name": "...", "techStack": "...", ... } ],
 *   "skills": [ { "name": "Java", "category": "Backend", "level": 4 } ],
 *   "languages": [ { "name": "English", "proficiency": "Fluent" } ]
 * }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CvImportRequest {

    private ProfileData profile;
    private List<ExperienceData> experiences;
    private List<EducationData> educations;
    private List<ProjectData> projects;
    private List<SkillData> skills;
    private List<LanguageData> languages;

    // ── Profile ───────────────────────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProfileData {
        private String title;
        private String summary;
        private String phone;
        private String location;

        @JsonProperty("linkedin_url")
        private String linkedinUrl;

        @JsonProperty("github_url")
        private String githubUrl;

        @JsonProperty("portfolio_url")
        private String portfolioUrl;
    }

    // ── Experience ────────────────────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExperienceData {
        private String company;
        private String role;
        private String location;

        @JsonProperty("start_date")
        private LocalDate startDate;

        @JsonProperty("end_date")
        private LocalDate endDate;

        private Boolean current;
        private String description;
    }

    // ── Education ─────────────────────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EducationData {
        private String institution;
        private String degree;

        @JsonProperty("field_of_study")
        private String fieldOfStudy;

        @JsonProperty("start_date")
        private LocalDate startDate;

        @JsonProperty("end_date")
        private LocalDate endDate;

        private Boolean current;
        private String description;
    }

    // ── Project ───────────────────────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProjectData {
        private String name;
        private String description;

        @JsonProperty("tech_stack")
        private String techStack;

        @JsonProperty("project_url")
        private String projectUrl;

        @JsonProperty("repo_url")
        private String repoUrl;
    }

    // ── Skill ─────────────────────────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkillData {
        private String name;
        private String category;
        private Integer level;
    }

    // ── Language ──────────────────────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LanguageData {
        private String name;
        private String proficiency;
    }
}