package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    // Find all educations belonging to a user's MasterCv
    @Query("""
        SELECT e FROM Education e
        JOIN e.masterCvs m
        WHERE m.user.id = :userId
    """)
    List<Education> findAllByUserId(@Param("userId") Long userId);
}
