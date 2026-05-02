package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;                    // e.g. "English"

    private String proficiency;            // e.g. "Native", "Fluent", "Intermediate", "Basic"

    // ── Relationships ────────────────────────────────────────────────────────

    @ManyToMany(mappedBy = "languages")
    private List<MasterCv> masterCvs;
}