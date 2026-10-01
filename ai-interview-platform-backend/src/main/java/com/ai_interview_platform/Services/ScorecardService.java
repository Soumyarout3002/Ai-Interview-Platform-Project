package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.CompleteInterviewRequest;
import com.ai_interview_platform.DTOs.ScorecardResponse;

import java.util.UUID;

public interface ScorecardService {

    ScorecardResponse completeInterview(UUID sessionId, CompleteInterviewRequest request);
    ScorecardResponse getScorecard(UUID sessionId);
}
