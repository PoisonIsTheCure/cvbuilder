package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;                    // e.g. "Spring Boot"

    private String category;               // e.g. "Backend", "DevOps", "Frontend"

    private Integer level;                 // 1–5 proficiency level (optional)

    // ── Relationships ────────────────────────────────────────────────────────

    @ManyToMany(mappedBy = "skills")
    private List<MasterCv> masterCvs;
}