package com.ai_interview_platform.DTOs;

public record CompleteInterviewRequest(

        Integer technicalScore,
        Integer communicationScore,
        Integer problemSolvingScore
) {
}
