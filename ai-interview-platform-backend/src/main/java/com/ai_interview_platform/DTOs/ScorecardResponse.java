package com.ai_interview_platform.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScorecardResponse(

        UUID id,
        UUID sessionId,
        Integer technicalScore,
        Integer communicationScore,
        Integer problemSolvingScore,
        Integer overallScore,
        String strengths,
        String weaknesses,
        String Feedback,
        LocalDateTime createdAt

) {
}
