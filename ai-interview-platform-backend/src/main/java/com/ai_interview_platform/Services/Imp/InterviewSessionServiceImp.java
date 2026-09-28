package com.ai_interview_platform.Services.Imp;

import com.ai_interview_platform.DTOs.InterviewSessionResponse;
import com.ai_interview_platform.DTOs.StartInterviewRequest;
import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.InterviewStatus;
import com.ai_interview_platform.Models.User;
import com.ai_interview_platform.Repositories.InterviewSessionRepository;
import com.ai_interview_platform.Repositories.UserRepository;
import com.ai_interview_platform.Services.InterviewSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class InterviewSessionServiceImp implements InterviewSessionService{

    private final InterviewSessionRepository interviewSessionRepository;
    private final UserRepository userRepository;
    @Override
    public InterviewSessionResponse startInterview(UUID userId, StartInterviewRequest request) {

        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User Not Found"));

        InterviewSession session = InterviewSession.builder()
                .user(user)
                .category(request.category())
                .difficulty(request.difficulty())
                .status(InterviewStatus.IN_PROCESS)
                .startedAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .build();
        InterviewSession interviewSession= interviewSessionRepository.save(session);
        return mapToResponse(interviewSession);

    }

    @Override
    public InterviewSessionResponse getInterviewById(UUID interviewId) {
        InterviewSession session= interviewSessionRepository.findById(interviewId).orElseThrow(()->
                new IllegalArgumentException("Interview Not Found"));
        return mapToResponse(session);
    }

    @Override
    public List<InterviewSessionResponse> getUserInterview(UUID userId) {

        User user =userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("User Not Found"));
        return interviewSessionRepository
                .findByUserOrderByCreateAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    private InterviewSessionResponse mapToResponse(InterviewSession session){
        return new InterviewSessionResponse(
                session.getId(),
                session.getUser().getId(),
                session.getCategory(),
                session.getDifficulty(),
                session.getStatus(),
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getCreateAt()

        );
    }
}
