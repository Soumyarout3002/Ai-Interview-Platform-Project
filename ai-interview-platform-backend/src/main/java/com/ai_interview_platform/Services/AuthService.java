package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.AuthResponse;
import com.ai_interview_platform.DTOs.CreateUserRequest;
import com.ai_interview_platform.DTOs.LoginRequest;
import com.ai_interview_platform.DTOs.UserResponse;

public interface AuthService {

    UserResponse register(CreateUserRequest request);

    AuthResponse login(LoginRequest request);
}
