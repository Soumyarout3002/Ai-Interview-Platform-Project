package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.QuestionAnswerResponse;
import com.ai_interview_platform.DTOs.SubmitAnswerRequest;

import java.util.List;
import java.util.UUID;

public interface QuestionAnswerService {
    QuestionAnswerResponse addQuestion(UUID sessionId ,String questionText , String difficulty);

    QuestionAnswerResponse submitAnswer(UUID questionAnswerId , SubmitAnswerRequest request);

    List<QuestionAnswerResponse> getSessionQuestions(UUID sessionId);
}