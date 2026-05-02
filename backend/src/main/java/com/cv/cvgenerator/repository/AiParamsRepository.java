package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.AiParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiParamsRepository extends JpaRepository<AiParams, Long> {

    Optional<AiParams> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
