package com.ai_interview_platform.Services;

public interface AiService {

    String generateQuestion(String category ,String difficulty);
    String evaluateAnswer(String category,String difficulty,String question,String answer);

}
