package com.ai_interview_platform.Repositories;

import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Models.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer,UUID> {
    List<QuestionAnswer>findBySessionOrderByQusetionNumber(InterviewSession session);
}
