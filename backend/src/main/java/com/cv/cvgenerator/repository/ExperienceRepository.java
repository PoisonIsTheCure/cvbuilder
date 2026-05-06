package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    @Query("""
        SELECT e FROM Experience e
        JOIN e.masterCvs m
        WHERE m.user.id = :userId
    """)
    List<Experience> findAllByUserId(@Param("userId") Long userId);

    // Used by TailoredCvService to fetch selected experiences for a specific user
    @Query("""
        SELECT e FROM Experience e
        JOIN e.masterCvs m
        WHERE m.user.id = :userId
        AND e.id IN :experienceIds
    """)
    List<Experience> findByUserIdAndIdIn(@Param("userId") Long userId,
                                          @Param("experienceIds") List<Long> experienceIds);
}
