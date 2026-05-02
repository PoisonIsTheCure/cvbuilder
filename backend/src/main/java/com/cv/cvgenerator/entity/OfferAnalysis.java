package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "offer_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer matchScore;             // 0–100 AI-generated match percentage

    @ElementCollection
    @CollectionTable(name = "offer_required_skills", joinColumns = @JoinColumn(name = "offer_analysis_id"))
    @Column(name = "skill")
    private List<String> requiredSkills;    // skills the offer demands

    @ElementCollection
    @CollectionTable(name = "offer_missing_skills", joinColumns = @JoinColumn(name = "offer_analysis_id"))
    @Column(name = "skill")
    private List<String> missingSkills;     // gap: required but not in MasterCv

    @ElementCollection
    @CollectionTable(name = "offer_suggested_projects", joinColumns = @JoinColumn(name = "offer_analysis_id"))
    @Column(name = "project_id")
    private List<Long> suggestedProjectIds; // project IDs from MasterCv to highlight

    @ElementCollection
    @CollectionTable(name = "offer_suggested_experiences", joinColumns = @JoinColumn(name = "offer_analysis_id"))
    @Column(name = "experience_id")
    private List<Long> suggestedExperienceIds;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;              // full AI-generated analysis text

    // ── Relationships ────────────────────────────────────────────────────────

    @OneToOne
    @JoinColumn(name = "job_application_id", nullable = false, unique = true)
    private JobApplication jobApplication;
}