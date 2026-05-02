package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        SELECT p FROM Project p
        JOIN p.masterCvs m
        WHERE m.user.id = :userId
    """)
    List<Project> findAllByUserId(@Param("userId") Long userId);

    // Used by OfferAnalysisService to fetch AI-suggested projects
    @Query("""
        SELECT p FROM Project p
        JOIN p.masterCvs m
        WHERE m.user.id = :userId
        AND p.id IN :projectIds
    """)
    List<Project> findByUserIdAndIdIn(@Param("userId") Long userId,
                                      @Param("projectIds") List<Long> projectIds);
}
