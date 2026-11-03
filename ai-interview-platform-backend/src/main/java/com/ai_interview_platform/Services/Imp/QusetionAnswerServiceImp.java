package com.ai_interview_platform.Services.Imp;

import ch.qos.logback.core.model.processor.AllowAllModelFilter;
import com.ai_interview_platform.DTOs.AIAnswerEvaluation;
import com.ai_interview_platform.DTOs.QuestionAnswerResponse;
import com.ai_interview_platform.DTOs.SubmitAnswerRequest;
import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.InterviewStatus;
import com.ai_interview_platform.Models.QuestionAnswer;
import com.ai_interview_platform.Repositories.InterviewSessionRepository;
import com.ai_interview_platform.Repositories.QuestionAnswerRepository;
import com.ai_interview_platform.Services.AiService;
import com.ai_interview_platform.Services.QuestionAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QusetionAnswerServiceImp implements QuestionAnswerService {

    @Value("${interview.max-questions:5}")
    private int maxQues;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final AiService aiService;

    @Override
    public QuestionAnswerResponse addQuestion(UUID sessionId,UUID userId, String questionText, String difficulty) {
        InterviewSession session = getOwnedSession(sessionId,userId);

        List<QuestionAnswer> existingQuestions =questionAnswerRepository
                .findBySessionOrderByQuestionNumber(session);
        int questionNumber = existingQuestions.size() + 1;

        QuestionAnswer questionAnswer = QuestionAnswer.builder()
                .session(session)
                .questionText(questionText)
                .questionNumber(questionNumber)
                .difficulty(difficulty)
                .build();
        QuestionAnswer saved=questionAnswerRepository.save(questionAnswer);
        return mapToResponse(saved);

    }

    @Override
    @Transactional
    public QuestionAnswerResponse generateQuestion(UUID sessionId,UUID userId) {

        InterviewSession session = getOwnedSession(sessionId,userId);

        if(session.getStatus() != InterviewStatus.IN_PROCESS) {
            throw new IllegalArgumentException(
                    "Questions can only be generated for an In-progress interview");
        }
        List<QuestionAnswer> existingQuestions =questionAnswerRepository
                .findBySessionOrderByQuestionNumber(session);

        if (existingQuestions.size() >= maxQues) {
            throw new IllegalStateException(
                    "Maximum question limit reached for this interview");
        }

        int questionNumber = existingQuestions.size() + 1;

        String question = aiService.generateQuestion(session.getCategory(),session.getDifficulty());

        QuestionAnswer questionAnswer = QuestionAnswer.builder()
                .session(session)
                .questionText(question)
                .questionNumber(questionNumber)
                .difficulty(session.getDifficulty())
                .build();
        QuestionAnswer saved=questionAnswerRepository.save(questionAnswer);

        return mapToResponse(saved);
    }

    private QuestionAnswerResponse mapToResponse(QuestionAnswer questionAnswer) {
        return new QuestionAnswerResponse(
                questionAnswer.getId(),
                questionAnswer.getSession().getId(),
                questionAnswer.getQuestionText(),
                questionAnswer.getAnswer(),
                questionAnswer.getQuestionNumber(),
                questionAnswer.getDifficulty(),
                questionAnswer.getAiFeedback(),
                questionAnswer.getAiScore(),
                questionAnswer.getSubmittedAt()

        );
    }

    @Override
    public QuestionAnswerResponse submitAnswer(UUID questionAnswerId,UUID userId, SubmitAnswerRequest request) {

        QuestionAnswer questionAnswer=questionAnswerRepository.findByIdAndSession_User_Id(questionAnswerId,userId).orElseThrow(()->
                new IllegalArgumentException("Question Not Found"));

        if(questionAnswer.getAnswer() != null){
            throw new IllegalArgumentException("Answer has already been submitted");
        }

        InterviewSession session = questionAnswer.getSession();

        AIAnswerEvaluation evaluation = aiService.evaluateAnswer(
                session.getCategory(),
                questionAnswer.getDifficulty(),
                questionAnswer.getQuestionText(),
                request.answer()

        );

        questionAnswer.setAnswer(request.answer());
        questionAnswer.setSubmittedAt(LocalDateTime.now());
        questionAnswer.setAiScore(evaluation.score());
        questionAnswer.setAiFeedback("""
                Strengths: %s
                
                Improvements: %s
                
                FeedBack: %s""".formatted(
                        evaluation.strengths(),
                evaluation.improvements(),
                evaluation.feedback()
        ));

        QuestionAnswer saved = questionAnswerRepository.save(questionAnswer);

        return mapToResponse(saved);
    }

    @Override
    public List<QuestionAnswerResponse> getSessionQuestions(UUID sessionId,UUID userId) {

        InterviewSession session = getOwnedSession(sessionId,userId);
        return questionAnswerRepository.findBySessionOrderByQuestionNumber(session)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "JAVA",
            "C",
            "C++",
            "PYTHON",
            "JAVASCRIPT",
            "TYPESCRIPT",
            "C#",
            "GO",
            "RUST",
            "SQL",
            "DSA",
            "DBMS",
            "OS",
            "NETWORKING",
            "OOP",
            "SYSTEM_DESIGN"
    );
    private static final Set<String> ALLOWED_DIFFICULTIES = Set.of(
            "EASY",
            "MEDIUM",
            "HARD"
    );

    private String validateCategory(String category) {
        String normalized = category.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_CATEGORIES.contains(normalized)){
            throw new IllegalArgumentException("Unsupported interview category:"+category);
        }
        return normalized;

        }

    private String validateDifficulty(String difficulty) {
        String normalized = difficulty.trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_DIFFICULTIES.contains(normalized)){
            throw new IllegalArgumentException("Unsupported interview category:"+difficulty);
        }
        return normalized;

    }
    private InterviewSession getOwnedSession(UUID sessionId, UUID userId) {

        return interviewSessionRepository
                .findByIdAndUser_Id(sessionId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview Session Not Found"
                ));
    }



}
