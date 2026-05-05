package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.JobApplicationRequest;
import com.cv.cvgenerator.dto.request.JobApplicationStatusRequest;
import com.cv.cvgenerator.dto.response.JobApplicationResponse;
import com.cv.cvgenerator.entity.JobApplication;
import com.cv.cvgenerator.entity.User;
import com.cv.cvgenerator.enums.JobApplicationStatus;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.exception.UnauthorizedException;
import com.cv.cvgenerator.repository.JobApplicationRepository;
import com.cv.cvgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    // ── Create ───────────────────────────────────────────────────────────────

    @Transactional
    public JobApplicationResponse create(Long userId, JobApplicationRequest request) {
        User user = findUser(userId);

        JobApplication jobApplication = JobApplication.builder()
                .role(request.getRole())
                .company(request.getCompany())
                .jobDescription(request.getJobDescription())
                .requirements(request.getRequirements())
                .jobUrl(request.getJobUrl())
                .status(request.getStatus())
                .dateApplied(request.getDateApplied())
                .user(user)
                .build();

        return toResponse(jobApplicationRepository.save(jobApplication));
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getAllByUser(Long userId) {
        return jobApplicationRepository.findAllByUserIdOrderByDateAppliedDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getAllByUserAndStatus(Long userId, JobApplicationStatus status) {
        return jobApplicationRepository.findAllByUserIdAndStatus(userId, status)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public JobApplicationResponse getById(Long userId, Long jobApplicationId) {
        return toResponse(findOwned(userId, jobApplicationId));
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Transactional
    public JobApplicationResponse update(Long userId, Long jobApplicationId, JobApplicationRequest request) {
        JobApplication jobApplication = findOwned(userId, jobApplicationId);

        jobApplication.setRole(request.getRole());
        jobApplication.setCompany(request.getCompany());
        jobApplication.setJobDescription(request.getJobDescription());
        jobApplication.setRequirements(request.getRequirements());
        jobApplication.setJobUrl(request.getJobUrl());
        jobApplication.setStatus(request.getStatus());
        jobApplication.setDateApplied(request.getDateApplied());

        return toResponse(jobApplicationRepository.save(jobApplication));
    }

    @Transactional
    public JobApplicationResponse updateStatus(Long userId, Long jobApplicationId,
                                                JobApplicationStatusRequest request) {
        JobApplication jobApplication = findOwned(userId, jobApplicationId);
        jobApplication.setStatus(request.getStatus());
        return toResponse(jobApplicationRepository.save(jobApplication));
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long userId, Long jobApplicationId) {
        jobApplicationRepository.delete(findOwned(userId, jobApplicationId));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JobApplication findOwned(Long userId, Long jobApplicationId) {
        return jobApplicationRepository.findByIdAndUserId(jobApplicationId, userId)
                .orElseThrow(() -> new UnauthorizedException(
                        "Job application not accessible: " + jobApplicationId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private JobApplicationResponse toResponse(JobApplication j) {
        return JobApplicationResponse.builder()
                .id(j.getId())
                .role(j.getRole())
                .company(j.getCompany())
                .jobDescription(j.getJobDescription())
                .requirements(j.getRequirements())
                .jobUrl(j.getJobUrl())
                .status(j.getStatus())
                .dateApplied(j.getDateApplied())
                .offerAnalysisId(j.getOfferAnalysis() != null ? j.getOfferAnalysis().getId() : null)
                .tailoredCvId(j.getTailoredCv() != null ? j.getTailoredCv().getId() : null)
                .build();
    }
}