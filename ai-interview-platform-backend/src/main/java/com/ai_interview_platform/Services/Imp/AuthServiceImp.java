package com.ai_interview_platform.Services.Imp;

import com.ai_interview_platform.DTOs.AuthResponse;
import com.ai_interview_platform.DTOs.CreateUserRequest;
import com.ai_interview_platform.DTOs.LoginRequest;
import com.ai_interview_platform.DTOs.UserResponse;
import com.ai_interview_platform.Models.User;
import com.ai_interview_platform.Repositories.UserRepository;
import com.ai_interview_platform.Security.JwtService;
import com.ai_interview_platform.Security.UserDetailsImp;
import com.ai_interview_platform.Services.AiService;
import com.ai_interview_platform.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long expiration;


    @Override
    public UserResponse register(CreateUserRequest request) {
        if (request.name() == null || request.name().isBlank()
                || request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("All fields are required");
        }

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return  new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(
                request.email().trim().toLowerCase()
        ).orElseThrow(() ->
                new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return createAuthResponse(user);
    }

    private AuthResponse createAuthResponse(User user) {

        UserDetailsImp userDetails = new UserDetailsImp(user);

        String token = jwtService.generateToken(userDetails,user.getId());

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

}
