package com.ai_interview_platform.Repositories;

import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {

    List<InterviewSession> findByUserOrderByCreateAtDesc(User user);

    Optional<InterviewSession> findByIdAndUser_Id(UUID id, UUID userId);

    List<InterviewSession> findByUser_IdOrderByCreateAtDesc(UUID userId);
}