package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private String degree;                  // e.g. "Bachelor of Science"

    private String fieldOfStudy;            // e.g. "Computer Science"

    private LocalDate startDate;
    private LocalDate endDate;              // null if ongoing

    private boolean current;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ── Relationships ────────────────────────────────────────────────────────

    @ManyToMany(mappedBy = "educations")
    private List<MasterCv> masterCvs;
}