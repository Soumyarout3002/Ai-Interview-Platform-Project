package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.AIAnswerEvaluation;

public interface AiService {

    String generateQuestion(String category ,String difficulty);
    AIAnswerEvaluation evaluateAnswer(String category, String difficulty, String question, String answer);

}
