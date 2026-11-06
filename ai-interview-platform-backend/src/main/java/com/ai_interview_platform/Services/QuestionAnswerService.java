package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.QuestionAnswerResponse;
import com.ai_interview_platform.DTOs.SubmitAnswerRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

public interface QuestionAnswerService {
    QuestionAnswerResponse addQuestion(UUID sessionId ,UUID userId,String questionText , String difficulty);

    QuestionAnswerResponse generateQuestion(UUID sessionId,UUID userId);

    SseEmitter generateQuestionStream(UUID sessionId, UUID userId);

    QuestionAnswerResponse submitAnswer(UUID questionAnswerId ,UUID userId, SubmitAnswerRequest request);

    List<QuestionAnswerResponse> getSessionQuestions(UUID sessionId,UUID userId);
}