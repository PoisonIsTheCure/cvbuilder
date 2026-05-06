package com.cv.cvgenerator.controller;

import com.cv.cvgenerator.dto.request.TailoredCvRequest;
import com.cv.cvgenerator.dto.response.TailoredCvResponse;
import com.cv.cvgenerator.security.SecurityUtils;
import com.cv.cvgenerator.service.CvGenerationService;
import com.cv.cvgenerator.service.TailoredCvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tailored-cvs")
@RequiredArgsConstructor
public class TailoredCvController {

    private final TailoredCvService tailoredCvService;
    private final CvGenerationService cvGenerationService;

    @GetMapping
    public ResponseEntity<List<TailoredCvResponse>> getAll() {
        return ResponseEntity.ok(tailoredCvService.getAllByUser(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TailoredCvResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tailoredCvService.getById(SecurityUtils.getCurrentUserId(), id));
    }

    @PostMapping
    public ResponseEntity<TailoredCvResponse> create(@Valid @RequestBody TailoredCvRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tailoredCvService.create(SecurityUtils.getCurrentUserId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tailoredCvService.delete(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ── PDF Export ───────────────────────────────────────────────────────────

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportToPdf(@PathVariable Long id) {
        byte[] pdf = cvGenerationService.generateFromTailoredCv(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"tailored-cv-" + id + ".pdf\"")
                .body(pdf);
    }
}
