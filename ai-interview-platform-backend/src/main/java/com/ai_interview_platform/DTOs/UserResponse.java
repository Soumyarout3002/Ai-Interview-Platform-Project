package com.ai_interview_platform.DTOs;

import java.util.UUID;

public record UserResponse(

        UUID id,
        String name,
        String email
) {
}
