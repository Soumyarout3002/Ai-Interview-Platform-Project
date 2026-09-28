package com.ai_interview_platform.DTOs;

public record CreateUserRequest(

        String name,
        String email,
        String password
) {
}
