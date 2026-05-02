package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.CvLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvLayoutRepository extends JpaRepository<CvLayout, Long> {

    Optional<CvLayout> findByName(String name);

    // Useful for letting users browse available layouts
    List<CvLayout> findAllByOrderByNameAsc();
}
