package com.ai_interview_platform.Services.Imp;

import com.ai_interview_platform.Services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImp implements AiService {
    private final ChatClient chatClient;
    @Override
    public String generateQuestion(String category, String difficulty) {
        System.out.println(">>> BEFORE GEMINI CALL");
        String prompt = """
                You are an experienced technical interviewer.
                
                Generate exactly one interview question.
                
                category : %s
                difficulty : %s
                
                Requirements :
                - The question must be suitable for a technical mock interview.
                -Do not provide the answer.
                -Do not provide explanations.
                - Return only the interview question.""".formatted(category,difficulty);
        String response= chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
        System.out.println(">>> AFTER GEMINI CALL");
        return response;
    }

    @Override
    public String evaluateAnswer(String category, String difficulty, String question, String answer) {
        String prompt = """
                You are an experienced technical interviewer.
                 Evaluate the candidate's answer to the interview question.
                
                            Category: %s
                            Difficulty: %s
                
                            Interview Question:
                            %s
                
                            Candidate Answer:
                            %s
                
                            Give a concise evaluation.
                
                            Start with a score from 0 to 100 in this exact format:
                            SCORE: <number>
                
                            Then provide useful feedback explaining:
                            - what the candidate answered correctly
                            - what is missing or incorrect
                            - how the answer could be improved
                
                            Do not generate a new question.""".formatted(category,difficulty,question,answer);
        String response= chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
        return response;
    }
}
