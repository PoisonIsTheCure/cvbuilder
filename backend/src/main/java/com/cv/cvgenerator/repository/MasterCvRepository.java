package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.MasterCv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterCvRepository extends JpaRepository<MasterCv, Long> {

    Optional<MasterCv> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    // Eagerly fetch all collections in one query to avoid N+1
    @Query("""
        SELECT m FROM MasterCv m
        LEFT JOIN FETCH m.profile
        LEFT JOIN FETCH m.educations
        LEFT JOIN FETCH m.experiences
        LEFT JOIN FETCH m.projects
        LEFT JOIN FETCH m.skills
        LEFT JOIN FETCH m.languages
        WHERE m.user.id = :userId
    """)
    Optional<MasterCv> findByUserIdWithFullDetails(@Param("userId") Long userId);
}
