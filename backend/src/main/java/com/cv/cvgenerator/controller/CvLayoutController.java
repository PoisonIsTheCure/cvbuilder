package com.cv.cvgenerator.controller;

import com.cv.cvgenerator.dto.request.CvLayoutRequest;
import com.cv.cvgenerator.dto.response.CvLayoutResponse;
import com.cv.cvgenerator.service.CvLayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cv-layouts")
@RequiredArgsConstructor
public class CvLayoutController {

    private final CvLayoutService cvLayoutService;

    @GetMapping
    public ResponseEntity<List<CvLayoutResponse>> getAll() {
        return ResponseEntity.ok(cvLayoutService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CvLayoutResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cvLayoutService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CvLayoutResponse> create(@Valid @RequestBody CvLayoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cvLayoutService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CvLayoutResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody CvLayoutRequest request) {
        return ResponseEntity.ok(cvLayoutService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cvLayoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
