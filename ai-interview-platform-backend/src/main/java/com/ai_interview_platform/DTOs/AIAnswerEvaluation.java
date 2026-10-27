package com.ai_interview_platform.DTOs;

public record AIAnswerEvaluation(
        Integer score,
        String strengths,
        String improvements,
        String feedback
) {}
