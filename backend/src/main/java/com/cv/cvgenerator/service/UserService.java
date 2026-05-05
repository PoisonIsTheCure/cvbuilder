package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.AiParamsRequest;
import com.cv.cvgenerator.dto.request.UpdateUserRequest;
import com.cv.cvgenerator.dto.response.AiParamsResponse;
import com.cv.cvgenerator.dto.response.UserResponse;
import com.cv.cvgenerator.entity.AiParams;
import com.cv.cvgenerator.entity.User;
import com.cv.cvgenerator.exception.ResourceNotFoundException;
import com.cv.cvgenerator.repository.AiParamsRepository;
import com.cv.cvgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AiParamsRepository aiParamsRepository;

    // ── Get current user ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = findUserById(userId);
        return toUserResponse(user);
    }

    // ── Update user ──────────────────────────────────────────────────────────

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = findUserById(userId);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());

        return toUserResponse(userRepository.save(user));
    }

    // ── AiParams ─────────────────────────────────────────────────────────────

    @Transactional
    public AiParamsResponse saveAiParams(Long userId, AiParamsRequest request) {
        User user = findUserById(userId);

        // Upsert — create or update
        AiParams aiParams = aiParamsRepository.findByUserId(userId)
                .orElse(AiParams.builder().user(user).build());

        aiParams.setApiKey(request.getApiKey());
        aiParams.setModel(request.getModel());
        aiParams.setBaseUrl(request.getBaseUrl());
        aiParams.setMaxTokens(request.getMaxTokens());
        aiParams.setTemperature(request.getTemperature());

        return toAiParamsResponse(aiParamsRepository.save(aiParams));
    }

    @Transactional(readOnly = true)
    public AiParamsResponse getAiParams(Long userId) {
        AiParams aiParams = aiParamsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("AiParams not found for user: " + userId));
        return toAiParamsResponse(aiParams);
    }

    @Transactional
    public void deleteAiParams(Long userId) {
        AiParams aiParams = aiParamsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("AiParams not found for user: " + userId));
        aiParamsRepository.delete(aiParams);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private AiParamsResponse toAiParamsResponse(AiParams aiParams) {
        return AiParamsResponse.builder()
                .id(aiParams.getId())
                .model(aiParams.getModel())
                .baseUrl(aiParams.getBaseUrl())
                .maxTokens(aiParams.getMaxTokens())
                .temperature(aiParams.getTemperature())
                .build();
    }
}
