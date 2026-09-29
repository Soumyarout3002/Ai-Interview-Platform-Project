package com.ai_interview_platform.Services.Imp;

import com.ai_interview_platform.DTOs.QuestionAnswerResponse;
import com.ai_interview_platform.DTOs.SubmitAnswerRequest;
import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.QuestionAnswer;
import com.ai_interview_platform.Repositories.InterviewSessionRepository;
import com.ai_interview_platform.Repositories.QuestionAnswerRepository;
import com.ai_interview_platform.Services.QuestionAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QusetionAnswerServiceImp implements QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    @Override
    public QuestionAnswerResponse addQuestion(UUID sessionId, String questionText, String difficulty) {
        InterviewSession session = interviewSessionRepository.findById(sessionId).orElseThrow(()
                ->new IllegalArgumentException("Interview Session Not Found"));

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
    public QuestionAnswerResponse submitAnswer(UUID questionAnswerId, SubmitAnswerRequest request) {

        QuestionAnswer questionAnswer=questionAnswerRepository.findById(questionAnswerId).orElseThrow(()->
                new IllegalArgumentException("Question Not Found"));
        questionAnswer.setAnswer(request.answer());
        questionAnswer.setSubmittedAt(LocalDateTime.now());

        QuestionAnswer saved = questionAnswerRepository.save(questionAnswer);

        return mapToResponse(saved);
    }

    @Override
    public List<QuestionAnswerResponse> getSessionQuestions(UUID sessionId) {

        InterviewSession session = interviewSessionRepository.findById(sessionId).orElseThrow(
                ()-> new IllegalArgumentException("Interview Session Not Found "));
        return questionAnswerRepository.findBySessionOrderByQuestionNumber(session)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
