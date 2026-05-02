package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Query("""
        SELECT s FROM Skill s
        JOIN s.masterCvs m
        WHERE m.user.id = :userId
    """)
    List<Skill> findAllByUserId(@Param("userId") Long userId);

    // Avoid duplicate skill names per user
    @Query("""
        SELECT s FROM Skill s
        JOIN s.masterCvs m
        WHERE m.user.id = :userId
        AND LOWER(s.name) = LOWER(:name)
    """)
    Optional<Skill> findByUserIdAndNameIgnoreCase(@Param("userId") Long userId,
                                                   @Param("name") String name);
}
