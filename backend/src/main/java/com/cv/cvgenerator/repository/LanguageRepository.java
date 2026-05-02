package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    @Query("""
        SELECT l FROM Language l
        JOIN l.masterCvs m
        WHERE m.user.id = :userId
    """)
    List<Language> findAllByUserId(@Param("userId") Long userId);
}
