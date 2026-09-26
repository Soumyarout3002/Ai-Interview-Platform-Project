package com.ai_interview_platform.Repositories;

import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {

    List<InterviewSession> findByUserOrderByCreatedAtDesc(User user);
}