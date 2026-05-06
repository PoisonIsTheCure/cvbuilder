package com.cv.cvgenerator.controller;

import com.cv.cvgenerator.dto.request.*;
import com.cv.cvgenerator.dto.response.*;
import com.cv.cvgenerator.security.SecurityUtils;
import com.cv.cvgenerator.service.CvEntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master-cv")
@RequiredArgsConstructor
public class CvEntityController {

    private final CvEntityService cvEntityService;

    // ── Profile ──────────────────────────────────────────────────────────────

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(cvEntityService.getProfile(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> saveProfile(@Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(cvEntityService.saveProfile(SecurityUtils.getCurrentUserId(), request));
    }

    // ── Education ────────────────────────────────────────────────────────────

    @GetMapping("/educations")
    public ResponseEntity<List<EducationResponse>> getEducations() {
        return ResponseEntity.ok(cvEntityService.getEducations(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/educations")
    public ResponseEntity<EducationResponse> addEducation(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cvEntityService.addEducation(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/educations/{id}")
    public ResponseEntity<EducationResponse> updateEducation(@PathVariable Long id,
                                                              @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(cvEntityService.updateEducation(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/educations/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        cvEntityService.deleteEducation(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Experience ───────────────────────────────────────────────────────────

    @GetMapping("/experiences")
    public ResponseEntity<List<ExperienceResponse>> getExperiences() {
        return ResponseEntity.ok(cvEntityService.getExperiences(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/experiences")
    public ResponseEntity<ExperienceResponse> addExperience(@Valid @RequestBody ExperienceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cvEntityService.addExperience(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/experiences/{id}")
    public ResponseEntity<ExperienceResponse> updateExperience(@PathVariable Long id,
                                                                @Valid @RequestBody ExperienceRequest request) {
        return ResponseEntity.ok(cvEntityService.updateExperience(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/experiences/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        cvEntityService.deleteExperience(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Projects ─────────────────────────────────────────────────────────────

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        return ResponseEntity.ok(cvEntityService.getProjects(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> addProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cvEntityService.addProject(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                          @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(cvEntityService.updateProject(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        cvEntityService.deleteProject(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Skills ───────────────────────────────────────────────────────────────

    @GetMapping("/skills")
    public ResponseEntity<List<SkillResponse>> getSkills() {
        return ResponseEntity.ok(cvEntityService.getSkills(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/skills")
    public ResponseEntity<SkillResponse> addSkill(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cvEntityService.addSkill(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable Long id,
                                                      @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(cvEntityService.updateSkill(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        cvEntityService.deleteSkill(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Languages ────────────────────────────────────────────────────────────

    @GetMapping("/languages")
    public ResponseEntity<List<LanguageResponse>> getLanguages() {
        return ResponseEntity.ok(cvEntityService.getLanguages(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/languages")
    public ResponseEntity<LanguageResponse> addLanguage(@Valid @RequestBody LanguageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cvEntityService.addLanguage(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/languages/{id}")
    public ResponseEntity<LanguageResponse> updateLanguage(@PathVariable Long id,
                                                            @Valid @RequestBody LanguageRequest request) {
        return ResponseEntity.ok(cvEntityService.updateLanguage(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/languages/{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        cvEntityService.deleteLanguage(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
