package com.cv.cvgenerator.controller;

import com.cv.cvgenerator.dto.request.JobApplicationRequest;
import com.cv.cvgenerator.dto.request.JobApplicationStatusRequest;
import com.cv.cvgenerator.dto.response.JobApplicationResponse;
import com.cv.cvgenerator.dto.response.OfferAnalysisResponse;
import com.cv.cvgenerator.enums.JobApplicationStatus;
import com.cv.cvgenerator.security.SecurityUtils;
import com.cv.cvgenerator.service.JobApplicationService;
import com.cv.cvgenerator.service.OfferAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final OfferAnalysisService offerAnalysisService;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAll(
            @RequestParam(required = false) JobApplicationStatus status) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (status != null) {
            return ResponseEntity.ok(jobApplicationService.getAllByUserAndStatus(userId, status));
        }
        return ResponseEntity.ok(jobApplicationService.getAllByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getById(SecurityUtils.getCurrentUserId(), id));
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(@Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobApplicationService.create(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> update(@PathVariable Long id,
                                                          @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.ok(jobApplicationService.update(SecurityUtils.getCurrentUserId(), id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationStatusRequest request) {
        return ResponseEntity.ok(
                jobApplicationService.updateStatus(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobApplicationService.delete(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Offer Analysis ───────────────────────────────────────────────────────

    @PostMapping("/{id}/analyse")
    public ResponseEntity<OfferAnalysisResponse> analyseOffer(@PathVariable Long id) {
        return ResponseEntity.ok(
                offerAnalysisService.analyseOffer(SecurityUtils.getCurrentUserId(), id));
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<OfferAnalysisResponse> getAnalysis(@PathVariable Long id) {
        return ResponseEntity.ok(
                offerAnalysisService.getByJobApplication(SecurityUtils.getCurrentUserId(), id));
    }
}
