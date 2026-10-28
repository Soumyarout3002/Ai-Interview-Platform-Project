package com.ai_interview_platform.DTOs;

public record AIInterviewSummary(
        Integer technicalScore,
        Integer communicationScore,
        Integer problemSolvingScore,
        String strengths,
        String weaknesses,
        String detailedFeedback
) {}
