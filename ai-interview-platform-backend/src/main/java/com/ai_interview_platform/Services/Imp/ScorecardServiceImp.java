package com.ai_interview_platform.Services.Imp;

import com.ai_interview_platform.DTOs.AIInterviewSummary;
import com.ai_interview_platform.DTOs.ScorecardResponse;
import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.InterviewStatus;
import com.ai_interview_platform.Models.QuestionAnswer;
import com.ai_interview_platform.Models.Scorecard;
import com.ai_interview_platform.Repositories.InterviewSessionRepository;
import com.ai_interview_platform.Repositories.QuestionAnswerRepository;
import com.ai_interview_platform.Repositories.ScorecardRepository;
import com.ai_interview_platform.Services.AiService;
import com.ai_interview_platform.Services.ScorecardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ScorecardServiceImp implements ScorecardService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final ScorecardRepository scorecardRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final AiService aiService;

    @Override
    public ScorecardResponse completeInterview(UUID sessionId) {

        InterviewSession session = interviewSessionRepository.findById(sessionId).orElseThrow(
                () -> new IllegalArgumentException("Interview Session Not Found")
        );
        if (session.getStatus() != InterviewStatus.IN_PROCESS) {
            throw new IllegalArgumentException("Only in-progress interview can be  completed");
        }

        if (scorecardRepository.findBySession(session).isPresent()) {
            throw new IllegalArgumentException("interview is already  completed");
        }

        List<QuestionAnswer> questions =
                questionAnswerRepository
                        .findBySessionOrderByQuestionNumber(session);

        if(questions.isEmpty()) {
            throw new IllegalArgumentException("Cannot complete an interview without questions");
        }
        boolean hasIncompleteAnswers = questions.stream()
                .anyMatch(questionAnswer -> questionAnswer.getAnswer()==null
                || questionAnswer.getAiScore()==null
                || questionAnswer.getAiFeedback()==null);

        if (hasIncompleteAnswers) {
            throw new IllegalArgumentException("All questions must be answered and evaluated");
        }
        String interviewHistory = questions.stream()
                .map(q -> """
                    Question %d: %s
                    Candidate answer: %s
                    Individual score: %d/100
                    Previous AI feedback: %s
                    """.formatted(
                        q.getQuestionNumber(),
                        q.getQuestionText(),
                        q.getAnswer(),
                        q.getAiScore(),
                        q.getAiFeedback()
                ))
                .collect(java.util.stream.Collectors.joining("\n"));

        AIInterviewSummary summary=aiService.generateInterviewSummary(
                session.getCategory(),
                session.getDifficulty(),
                interviewHistory
        );

        double averageScore = questions.stream()
                .mapToInt(QuestionAnswer::getAiScore).average().orElseThrow();

        int overallScore=(int) Math.round(averageScore);

        Scorecard scorecard = Scorecard.builder()
                .session(session)
                .technicalScore(summary.technicalScore())
                .communicationScore(summary.communicationScore())
                .problemSolvingScore(summary.problemSolvingScore())
                .overallScore(overallScore)
                .strengths(summary.strengths())
                .weaknesses(summary.weaknesses())
                .feedback(summary.detailedFeedback())
                .createdAt(LocalDateTime.now())
                .build();

        Scorecard save = scorecardRepository.save(scorecard);
        session.setStatus(InterviewStatus.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());

        interviewSessionRepository.save(session);

        return mapToResponse(save);

    }

    private ScorecardResponse mapToResponse(Scorecard scorecard) {
        return new ScorecardResponse(
                scorecard.getId(),
                scorecard.getSession().getId(),
                scorecard.getTechnicalScore(),
                scorecard.getProblemSolvingScore(),
                scorecard.getOverallScore(),
                scorecard.getStrengths(),
                scorecard.getWeaknesses(),
                scorecard.getFeedback(),
                scorecard.getCreatedAt()

        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScorecardResponse getScorecard(UUID sessionId) {

        InterviewSession session = interviewSessionRepository.findById(sessionId).orElseThrow(()->
                new IllegalArgumentException("Interview Session Not Found"));

        Scorecard scorecard= scorecardRepository.findBySession(session).orElseThrow(()->
                new IllegalArgumentException("Scorecard Not Found"));

        return mapToResponse(scorecard);

    }



}
