package com.cv.cvgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_params")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiParams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String apiKey;

    @Column(nullable = false)
    private String model;                   // e.g. "openai/gpt-4o", "mistralai/mixtral-8x7b"

    private String baseUrl;                 // OpenRouter base URL (can be overridden)

    private Integer maxTokens;

    private Double temperature;

    // ── Relationships ────────────────────────────────────────────────────────

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}