package com.ai_interview_platform.Services;

import com.ai_interview_platform.DTOs.AIAnswerEvaluation;
import com.ai_interview_platform.DTOs.AIInterviewSummary;

public interface AiService {

    String generateQuestion(String category ,String difficulty);
    AIAnswerEvaluation evaluateAnswer(String category, String difficulty, String question, String answer);
    AIInterviewSummary generateInterviewSummary(String category, String difficulty,String interviewHistory);

}
