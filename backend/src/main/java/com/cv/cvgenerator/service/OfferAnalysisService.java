package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.response.AiAnalysisResult;
import com.cv.cvgenerator.dto.response.OfferAnalysisResponse;
import com.cv.cvgenerator.entity.*;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.repository.JobApplicationRepository;
import com.cv.cvgenerator.repository.OfferAnalysisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferAnalysisService {

    private final OfferAnalysisRepository offerAnalysisRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final MasterCvService masterCvService;
    private final AiRequestsService aiRequestsService;
    private final ObjectMapper objectMapper;

    // ── Analyse offer ────────────────────────────────────────────────────────

    @Transactional
    public OfferAnalysisResponse analyseOffer(Long userId, Long jobApplicationId) {
        // Prevent duplicate analysis — delete existing if re-analysing
        if (offerAnalysisRepository.existsByJobApplicationId(jobApplicationId)) {
            offerAnalysisRepository.deleteById(
                    offerAnalysisRepository.findByJobApplicationId(jobApplicationId).get().getId()
            );
        }

        JobApplication jobApplication = jobApplicationRepository
                .findByIdAndUserId(jobApplicationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job application not accessible: " + jobApplicationId));

        MasterCv masterCv = masterCvService.getMasterCvEntity(userId);

        // Build prompts
        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(jobApplication, masterCv);

        // Call AI
        log.info("Sending offer analysis request to AI for job application: {}", jobApplicationId);
        String aiRawResponse = aiRequestsService.sendPrompt(userId, systemPrompt, userPrompt);

        // Parse AI response
        AiAnalysisResult result = parseAiResponse(aiRawResponse);

        // Persist analysis
        OfferAnalysis offerAnalysis = OfferAnalysis.builder()
                .jobApplication(jobApplication)
                .matchScore(result.getMatchScore())
                .requiredSkills(result.getRequiredSkills())
                .missingSkills(result.getMissingSkills())
                .suggestedProjectIds(result.getSuggestedProjectIds())
                .suggestedExperienceIds(result.getSuggestedExperienceIds())
                .aiSummary(result.getSummary())
                .build();

        return toResponse(offerAnalysisRepository.save(offerAnalysis));
    }

    // ── Get existing analysis ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public OfferAnalysisResponse getByJobApplication(Long userId, Long jobApplicationId) {
        // Verify ownership
        jobApplicationRepository.findByIdAndUserId(jobApplicationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job application not accessible: " + jobApplicationId));

        return offerAnalysisRepository.findByJobApplicationId(jobApplicationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No analysis found for job application: " + jobApplicationId));
    }

    // ── Prompt builders ──────────────────────────────────────────────────────

    private String buildSystemPrompt() {
        return """
                You are an expert career coach and recruitment analyst.
                Your job is to analyse a job offer against a candidate's CV and return a structured JSON response.
                
                You MUST respond with ONLY a valid JSON object matching this exact structure:
                {
                  "match_score": <integer 0-100>,
                  "required_skills": [<list of skills the job requires>],
                  "missing_skills": [<skills required but absent from the CV>],
                  "suggested_project_ids": [<IDs of projects from the CV most relevant to this offer>],
                  "suggested_experience_ids": [<IDs of experiences from the CV most relevant to this offer>],
                  "summary": "<2-3 sentence analysis of the match>"
                }
                
                Rules:
                - match_score must reflect how well the candidate's skills and experience match the offer
                - suggested_project_ids and suggested_experience_ids must only contain IDs provided in the CV data
                - missing_skills must only include skills explicitly required in the job offer
                - Do not include any text outside the JSON object
                """;
    }

    private String buildUserPrompt(JobApplication jobApplication, MasterCv masterCv) {
        StringBuilder sb = new StringBuilder();

        sb.append("## JOB OFFER\n");
        sb.append("Role: ").append(jobApplication.getRole()).append("\n");
        sb.append("Company: ").append(jobApplication.getCompany()).append("\n");
        if (jobApplication.getJobDescription() != null) {
            sb.append("Description: ").append(jobApplication.getJobDescription()).append("\n");
        }
        if (jobApplication.getRequirements() != null) {
            sb.append("Requirements: ").append(jobApplication.getRequirements()).append("\n");
        }

        sb.append("\n## CANDIDATE CV\n");

        // Skills
        if (masterCv.getSkills() != null && !masterCv.getSkills().isEmpty()) {
            sb.append("Skills: ");
            masterCv.getSkills().forEach(s ->
                    sb.append(s.getName()).append(" (").append(s.getCategory()).append("), "));
            sb.append("\n");
        }

        // Experiences
        if (masterCv.getExperiences() != null && !masterCv.getExperiences().isEmpty()) {
            sb.append("\nExperiences:\n");
            masterCv.getExperiences().forEach(e ->
                    sb.append("- [ID:").append(e.getId()).append("] ")
                            .append(e.getRole()).append(" at ").append(e.getCompany())
                            .append(": ").append(e.getDescription()).append("\n"));
        }

        // Projects
        if (masterCv.getProjects() != null && !masterCv.getProjects().isEmpty()) {
            sb.append("\nProjects:\n");
            masterCv.getProjects().forEach(p ->
                    sb.append("- [ID:").append(p.getId()).append("] ")
                            .append(p.getName()).append(" (").append(p.getTechStack()).append(")")
                            .append(": ").append(p.getDescription()).append("\n"));
        }

        // Education
        if (masterCv.getEducations() != null && !masterCv.getEducations().isEmpty()) {
            sb.append("\nEducation:\n");
            masterCv.getEducations().forEach(e ->
                    sb.append("- ").append(e.getDegree()).append(" in ")
                            .append(e.getFieldOfStudy()).append(" at ").append(e.getInstitution()).append("\n"));
        }

        return sb.toString();
    }

    // ── AI response parsing ──────────────────────────────────────────────────

    private AiAnalysisResult parseAiResponse(String rawResponse) {
        try {
            // Strip potential markdown code fences if model ignores instructions
            String cleaned = rawResponse
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
            return objectMapper.readValue(cleaned, AiAnalysisResult.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", rawResponse);
            throw new RuntimeException("Failed to parse AI analysis response: " + e.getMessage());
        }
    }

    // ── Response mapper ──────────────────────────────────────────────────────

    private OfferAnalysisResponse toResponse(OfferAnalysis o) {
        return OfferAnalysisResponse.builder()
                .id(o.getId())
                .jobApplicationId(o.getJobApplication().getId())
                .matchScore(o.getMatchScore())
                .requiredSkills(o.getRequiredSkills())
                .missingSkills(o.getMissingSkills())
                .suggestedProjectIds(o.getSuggestedProjectIds())
                .suggestedExperienceIds(o.getSuggestedExperienceIds())
                .aiSummary(o.getAiSummary())
                .build();
    }
}