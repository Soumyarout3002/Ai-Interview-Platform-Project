package com.ai_interview_platform.Repositories;

import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.Scorecard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScorecardRepository extends JpaRepository<Scorecard, UUID> {

    Optional<Scorecard>findBySession(InterviewSession session);
    
}
