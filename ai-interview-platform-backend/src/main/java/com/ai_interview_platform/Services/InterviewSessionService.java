package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.InterviewSessionResponse;
import com.ai_interview_platform.DTOs.StartInterviewRequest;
import com.ai_interview_platform.Models.InterviewSession;

import java.util.List;
import java.util.UUID;

public interface InterviewSessionService {

    InterviewSessionResponse startInterview(UUID userId , StartInterviewRequest request);
    InterviewSessionResponse getInterviewById(UUID interviewId);
    List<InterviewSessionResponse>getUserInterview(UUID userId);
}
