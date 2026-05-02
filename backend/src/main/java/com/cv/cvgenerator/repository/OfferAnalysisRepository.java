package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.OfferAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferAnalysisRepository extends JpaRepository<OfferAnalysis, Long> {

    Optional<OfferAnalysis> findByJobApplicationId(Long jobApplicationId);

    boolean existsByJobApplicationId(Long jobApplicationId);
}
