package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.TailoredCv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TailoredCvRepository extends JpaRepository<TailoredCv, Long> {

    List<TailoredCv> findAllByUserId(Long userId);

    Optional<TailoredCv> findByJobApplicationId(Long jobApplicationId);

    // Fetch with all filtered subsets to avoid N+1 on PDF generation
    @Query("""
        SELECT t FROM TailoredCv t
        LEFT JOIN FETCH t.experiences
        LEFT JOIN FETCH t.projects
        LEFT JOIN FETCH t.skills
        LEFT JOIN FETCH t.cvLayout
        WHERE t.id = :id
    """)
    Optional<TailoredCv> findByIdWithFullDetails(@Param("id") Long id);
}
