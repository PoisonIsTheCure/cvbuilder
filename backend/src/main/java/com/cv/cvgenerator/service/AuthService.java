package com.cv.cvgenerator.service;

import com.cv.cvgenerator.dto.request.LoginRequest;
import com.cv.cvgenerator.dto.request.RegisterRequest;
import com.cv.cvgenerator.dto.response.AuthResponse;
import com.cv.cvgenerator.entity.MasterCv;
import com.cv.cvgenerator.entity.User;
import com.cv.cvgenerator.exception.ConflictException;
import com.cv.cvgenerator.repository.MasterCvRepository;
import com.cv.cvgenerator.repository.UserRepository;
import com.cv.cvgenerator.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MasterCvRepository masterCvRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // ── Register ─────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Auto-create an empty MasterCv for the new user
        MasterCv masterCv = MasterCv.builder()
                .user(user)
                .build();
        masterCvRepository.save(masterCv);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return buildAuthResponse(token, user);
    }

    // ── Login ────────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user.getUsername());
        return buildAuthResponse(token, user);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
