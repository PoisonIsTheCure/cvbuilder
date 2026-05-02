package com.cv.cvgenerator.entity;

import com.cv.cvgenerator.enums.JobApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    private String company;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private String jobUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobApplicationStatus status;

    private LocalDate dateApplied;

    // ── Relationships ────────────────────────────────────────────────────────

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "jobApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private OfferAnalysis offerAnalysis;

    @OneToOne(mappedBy = "jobApplication")
    private TailoredCv tailoredCv;
}