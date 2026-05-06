package com.cv.cvgenerator.controller;

import com.cv.cvgenerator.dto.request.CvImportRequest;
import com.cv.cvgenerator.dto.response.MasterCvResponse;
import com.cv.cvgenerator.security.SecurityUtils;
import com.cv.cvgenerator.service.CvGenerationService;
import com.cv.cvgenerator.service.JsonParsingService;
import com.cv.cvgenerator.service.MasterCvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/master-cv")
@RequiredArgsConstructor
public class MasterCvController {

    private final MasterCvService masterCvService;
    private final JsonParsingService jsonParsingService;
    private final CvGenerationService cvGenerationService;

    // ── Get full MasterCv ────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<MasterCvResponse> getMasterCv() {
        return ResponseEntity.ok(masterCvService.getMasterCv(SecurityUtils.getCurrentUserId()));
    }

    // ── JSON Import ──────────────────────────────────────────────────────────

    @PostMapping("/import")
    public ResponseEntity<MasterCvResponse> importFromJson(@RequestBody CvImportRequest request) {
        return ResponseEntity.ok(
                jsonParsingService.importFromRequest(SecurityUtils.getCurrentUserId(), request));
    }

    @PostMapping("/import/file")
    public ResponseEntity<MasterCvResponse> importFromFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                jsonParsingService.importFromFile(SecurityUtils.getCurrentUserId(), file));
    }

    // ── JSON Export ──────────────────────────────────────────────────────────

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToJson() {
        byte[] data = jsonParsingService.exportToJson(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"master-cv.json\"")
                .body(data);
    }

    // ── PDF Export ───────────────────────────────────────────────────────────

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] pdf = cvGenerationService.generateFromMasterCv(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"master-cv.pdf\"")
                .body(pdf);
    }
}
