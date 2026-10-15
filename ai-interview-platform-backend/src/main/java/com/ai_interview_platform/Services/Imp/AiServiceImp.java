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

        String prompt = """
                You are an experinced technical interviewer.
                
                Generate exactly one interview question.
                
                category : %s
                difficulty : %s
                
                Requirements :
                - The question must be suitable for a technical mock interview.
                -Do not provide the answer.
                -Do not provide explanations.
                - Return only the interview question.""".formatted(category,difficulty);
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}
