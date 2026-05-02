package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cv_layouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;                    // e.g. "Modern Blue", "Classic Minimal"

    // ── Visual ───────────────────────────────────────────────────────────────

    private String primaryColor;            // hex e.g. "#2563EB"
    private String secondaryColor;
    private String fontFamily;             // e.g. "Inter", "Georgia"
    private String templateCode;           // identifier for the HTML/PDF template

    // ── Content Structure ────────────────────────────────────────────────────

    @ElementCollection
    @CollectionTable(name = "cv_layout_section_order", joinColumns = @JoinColumn(name = "cv_layout_id"))
    @Column(name = "section")
    @OrderColumn(name = "position")
    private List<String> sectionOrder;     // e.g. ["PROFILE", "EXPERIENCE", "EDUCATION", "SKILLS"]

    @ElementCollection
    @CollectionTable(name = "cv_layout_visible_sections", joinColumns = @JoinColumn(name = "cv_layout_id"))
    @Column(name = "section")
    private List<String> visibleSections;  // subset of sectionOrder that should be rendered

    // ── Relationships ────────────────────────────────────────────────────────

    @OneToMany(mappedBy = "cvLayout")
    private List<TailoredCv> tailoredCvs;
}