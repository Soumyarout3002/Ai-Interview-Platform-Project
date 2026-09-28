package com.ai_interview_platform.DTOs;

import com.ai_interview_platform.Models.InterviewStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewSessionResponse(
        UUID id,
        UUID userId,
        String category,
        String difficulty,
        InterviewStatus status,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt

) {
}
