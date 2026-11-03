package com.ai_interview_platform.DTOs;

import java.util.UUID;

public record AuthResponse(
        String token,
        String tokentype,
        UUID userId,
        String name,
        String email
) {
}
