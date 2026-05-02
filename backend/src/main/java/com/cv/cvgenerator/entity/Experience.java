package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    private String location;

    private LocalDate startDate;
    private LocalDate endDate;              // null if current job

    private boolean current;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ── Relationships ────────────────────────────────────────────────────────

    @ManyToMany(mappedBy = "experiences")
    private List<MasterCv> masterCvs;
}