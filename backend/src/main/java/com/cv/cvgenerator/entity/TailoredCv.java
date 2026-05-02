package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tailored_cvs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TailoredCv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String generatedPdfPath;        // path or URL to the generated PDF

    // ── Relationships ────────────────────────────────────────────────────────

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "master_cv_id", nullable = false)
    private MasterCv masterCv;

    @OneToOne
    @JoinColumn(name = "job_application_id")
    private JobApplication jobApplication;

    @ManyToOne
    @JoinColumn(name = "cv_layout_id")
    private CvLayout cvLayout;

    // ── Filtered subsets from MasterCv ───────────────────────────────────────

    @ManyToMany
    @JoinTable(
        name = "tailored_cv_experiences",
        joinColumns = @JoinColumn(name = "tailored_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "experience_id")
    )
    private List<Experience> experiences;

    @ManyToMany
    @JoinTable(
        name = "tailored_cv_projects",
        joinColumns = @JoinColumn(name = "tailored_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects;

    @ManyToMany
    @JoinTable(
        name = "tailored_cv_skills",
        joinColumns = @JoinColumn(name = "tailored_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;
}