package com.cv.cvgenerator.controller;

import com.cv.cvgenerator.dto.request.AiParamsRequest;
import com.cv.cvgenerator.dto.request.UpdateUserRequest;
import com.cv.cvgenerator.dto.response.AiParamsResponse;
import com.cv.cvgenerator.dto.response.UserResponse;
import com.cv.cvgenerator.security.SecurityUtils;
import com.cv.cvgenerator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── Profile ──────────────────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser(SecurityUtils.getCurrentUserId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(SecurityUtils.getCurrentUserId(), request));
    }

    // ── AI Params ────────────────────────────────────────────────────────────

    @GetMapping("/me/ai-params")
    public ResponseEntity<AiParamsResponse> getAiParams() {
        return ResponseEntity.ok(userService.getAiParams(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/me/ai-params")
    public ResponseEntity<AiParamsResponse> saveAiParams(@Valid @RequestBody AiParamsRequest request) {
        return ResponseEntity.ok(userService.saveAiParams(SecurityUtils.getCurrentUserId(), request));
    }

    @DeleteMapping("/me/ai-params")
    public ResponseEntity<Void> deleteAiParams() {
        userService.deleteAiParams(SecurityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
