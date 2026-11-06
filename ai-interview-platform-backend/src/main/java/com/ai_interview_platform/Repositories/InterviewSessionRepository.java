package com.ai_interview_platform.Repositories;

import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {

    List<InterviewSession> findByUserOrderByCreateAtDesc(User user);

    Optional<InterviewSession> findByIdAndUser_Id(UUID id, UUID userId);

    List<InterviewSession> findByUser_IdOrderByCreateAtDesc(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select s
        from InterviewSession s
        where s.id = :id
        and s.user.id = :userId
        """)
    Optional<InterviewSession> findByIdAndUserIdForUpdate(
            @Param("id") UUID id,
            @Param("userId") UUID userId);
}