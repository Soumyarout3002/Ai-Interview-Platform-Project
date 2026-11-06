package com.ai_interview_platform.Services.Imp;


import com.ai_interview_platform.DTOs.AIAnswerEvaluation;
import com.ai_interview_platform.DTOs.QuestionAnswerResponse;
import com.ai_interview_platform.DTOs.QuestionGenerationContext;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class QusetionAnswerServiceImp implements QuestionAnswerService {

    @Value("${interview.max-questions:5}")
    private int maxQues;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final TransactionTemplate transactionTemplate;
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

    @Override
    public SseEmitter generateQuestionStream(UUID sessionId, UUID userId) {

        QuestionGenerationContext context = getGenerationContext(sessionId,userId);

        SseEmitter emitter=new SseEmitter(120_000L);

        AtomicBoolean cancelled= new AtomicBoolean(false);

        Disposable.Swap subscription= Disposables.swap();

        Runnable cancelStream= () -> {
            cancelled.set(true);
            subscription.dispose();
        };
        emitter.onCompletion(cancelStream);
        emitter.onTimeout(cancelStream);
        emitter.onError(error -> cancelStream.run());

        StringBuilder generatedQuestion = new StringBuilder();

        try {
            Disposable disposable = aiService
                    .generateQuestionStream(
                            context.category(),
                            context.difficulty()
                    )
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(
                            chunk -> {
                                if (cancelled.get()) {
                                    return;
                                }

                                generatedQuestion.append(chunk);

                                try {
                                    emitter.send(
                                            SseEmitter.event()
                                                    .name("token")
                                                    .data(chunk)
                                    );
                                } catch (IOException e) {
                                    cancelStream.run();
                                }
                            },

                            error -> {
                                if (!cancelled.get()) {
                                    sendStreamError(emitter, cancelled);
                                }
                            },

                            () -> {
                                if (cancelled.get()) {
                                    return;
                                }

                                try {
                                    String question =
                                            generatedQuestion.toString().trim();

                                    if (question.isBlank()) {
                                        sendStreamError(emitter, cancelled);
                                        return;
                                    }

                                    QuestionAnswerResponse saved =
                                            saveGeneratedQuestion(
                                                    sessionId,
                                                    userId,
                                                    question
                                            );

                                    if (cancelled.get()) {
                                        return;
                                    }

                                    emitter.send(
                                            SseEmitter.event()
                                                    .name("complete")
                                                    .data(saved)
                                    );

                                    emitter.complete();

                                } catch (Exception e) {
                                    if (!cancelled.get()) {
                                        sendStreamError(emitter, cancelled);
                                    }
                                }
                            }
                    );

            subscription.update(disposable);

        } catch (RuntimeException e) {
            if (!cancelled.get()) {
                sendStreamError(emitter, cancelled);
            }
        }

        return emitter;
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

    private QuestionGenerationContext getGenerationContext(
            UUID sessionId,
            UUID userId
    ) {

        return transactionTemplate.execute(status -> {

            InterviewSession session = getOwnedSession(sessionId, userId);

            if (session.getStatus() != InterviewStatus.IN_PROCESS) {
                throw new IllegalArgumentException(
                        "Questions can only be generated for an In-progress interview"
                );
            }

            List<QuestionAnswer> existingQuestions =
                    questionAnswerRepository
                            .findBySessionOrderByQuestionNumber(session);

            if (existingQuestions.size() >= maxQues) {
                throw new IllegalStateException(
                        "Maximum question limit reached for this interview"
                );
            }

            return new QuestionGenerationContext(
                    session.getCategory(),
                    session.getDifficulty()
            );
        });
    }

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

    private void sendStreamError(
            SseEmitter emitter,
            AtomicBoolean cancelled
    ) {

        if (cancelled.get()) {
            return;
        }

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("error")
                            .data("Question generation failed. Please try again.")
            );
        } catch (IOException ignored) {

        } finally {
            emitter.complete();
        }
    }

    private QuestionAnswerResponse saveGeneratedQuestion(
            UUID sessionId,
            UUID userId,
            String questionText
    ) {

        return transactionTemplate.execute(status -> {

            InterviewSession session = interviewSessionRepository
                    .findByIdAndUserIdForUpdate(sessionId, userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Interview Session Not Found"
                    ));

            if (session.getStatus() != InterviewStatus.IN_PROCESS) {
                throw new IllegalArgumentException(
                        "Questions can only be generated for an In-progress interview"
                );
            }

            List<QuestionAnswer> existingQuestions =
                    questionAnswerRepository
                            .findBySessionOrderByQuestionNumber(session);

            if (existingQuestions.size() >= maxQues) {
                throw new IllegalStateException(
                        "Maximum question limit reached for this interview"
                );
            }

            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .session(session)
                    .questionText(questionText)
                    .questionNumber(existingQuestions.size() + 1)
                    .difficulty(session.getDifficulty())
                    .build();

            QuestionAnswer saved =
                    questionAnswerRepository.save(questionAnswer);

            return mapToResponse(saved);
        });
    }



}
