package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;                   // e.g. "Senior Backend Developer"

    @Column(columnDefinition = "TEXT")
    private String summary;                 // professional bio / about me

    private String phone;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;

    // ── Relationships ────────────────────────────────────────────────────────

    @OneToOne
    @JoinColumn(name = "master_cv_id", nullable = false, unique = true)
    private MasterCv masterCv;
}