package com.ai_interview_platform.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuestionAnswerResponse(
        UUID id,
        UUID sessionId,
        String questionText,
        String answer,
        Integer questionNumber,
        String difficulty,
        String aiFeedback,
        Integer aiScore,
        LocalDateTime submittedAt
) {
}
