package com.ai_interview_platform.Services.Imp;

import com.ai_interview_platform.DTOs.AIAnswerEvaluation;
import com.ai_interview_platform.DTOs.AIInterviewSummary;
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
    public AIAnswerEvaluation evaluateAnswer(String category, String difficulty, String question, String answer) {
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
        AIAnswerEvaluation evaluation = chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AIAnswerEvaluation.class,sp->sp.useProviderStructuredOutput()
                        .validateSchema());
        if (evaluation == null || evaluation.score() == null
                || evaluation.score()<0
                || evaluation.score()>100
                ||evaluation.strengths() == null
                || evaluation.improvements() == null
                || evaluation.feedback() == null){
            throw new IllegalArgumentException("AI Invalid evaluation");
        }

        return evaluation;
    }

    @Override
    public AIInterviewSummary generateInterviewSummary(String category, String difficulty, String interviewHistory) {

         String prompt = """
            You are an experienced technical interviewer.

            Evaluate the candidate's complete mock interview.

            Category: %s
            Difficulty: %s

            Interview history, including questions, answers,
            and previous AI evaluations:
            %s

            Return:
            - technicalScore: integer from 0 to 100
            - communicationScore: integer from 0 to 100
            - problemSolvingScore: integer from 0 to 100
            - strengths: evidence-based strengths
            - weaknesses: areas needing improvement
            - detailedFeedback: actionable overall feedback

            Score only using evidence in the interview history.
            Do not invent answers or accomplishments.
            If evidence for a category is limited, reflect that
            limitation in the feedback.
            """.formatted(category, difficulty, interviewHistory);

        AIInterviewSummary summary = chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(
                        AIInterviewSummary.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema()
                );

        if (summary == null
                || !isValidScore(summary.technicalScore())
                || !isValidScore(summary.communicationScore())
                || !isValidScore(summary.problemSolvingScore())
                || summary.strengths() == null
                || summary.weaknesses() == null
                || summary.detailedFeedback() == null) {

            throw new IllegalStateException(
                    "AI returned an invalid interview summary"
            );
        }

        return summary;
    }
    private boolean isValidScore(Integer score) {
        return score != null && score >= 0 && score <= 100;
    }

}
