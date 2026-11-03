package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.ScorecardResponse;

import java.util.UUID;

public interface ScorecardService {

    ScorecardResponse completeInterview(UUID sessionId, UUID userId);
    ScorecardResponse getScorecard(UUID sessionId , UUID userId);
}
