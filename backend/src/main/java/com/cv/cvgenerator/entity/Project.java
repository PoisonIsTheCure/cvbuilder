package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String techStack;               // e.g. "Spring Boot, Angular, PostgreSQL"

    private String projectUrl;
    private String repoUrl;

    // ── Relationships ────────────────────────────────────────────────────────

    @ManyToMany(mappedBy = "projects")
    private List<MasterCv> masterCvs;
}