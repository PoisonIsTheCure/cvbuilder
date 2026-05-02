package com.cv.cvgenerator.repository;

import com.cv.cvgenerator.entity.JobApplication;
import com.cv.cvgenerator.enums.JobApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findAllByUserId(Long userId);

    // Filter by status for job tracking board
    List<JobApplication> findAllByUserIdAndStatus(Long userId, JobApplicationStatus status);

    // Ordered by most recent first
    List<JobApplication> findAllByUserIdOrderByDateAppliedDesc(Long userId);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
}
