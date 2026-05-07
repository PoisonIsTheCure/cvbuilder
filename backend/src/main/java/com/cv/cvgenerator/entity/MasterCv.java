package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "master_cvs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterCv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Relationships ────────────────────────────────────────────────────────

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(mappedBy = "masterCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "master_cv_educations",
        joinColumns = @JoinColumn(name = "master_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "education_id")
    )
    private List<Education> educations = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "master_cv_experiences",
        joinColumns = @JoinColumn(name = "master_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "experience_id")
    )
    private List<Experience> experiences = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "master_cv_projects",
        joinColumns = @JoinColumn(name = "master_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "master_cv_skills",
        joinColumns = @JoinColumn(name = "master_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "master_cv_languages",
        joinColumns = @JoinColumn(name = "master_cv_id"),
        inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private List<Language> languages = new ArrayList<>();
}