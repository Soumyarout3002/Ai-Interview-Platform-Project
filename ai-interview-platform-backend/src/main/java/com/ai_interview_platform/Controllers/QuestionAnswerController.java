package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.QuestionAnswerResponse;
import com.ai_interview_platform.DTOs.SubmitAnswerRequest;
import com.ai_interview_platform.Services.QuestionAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionAnswerController {

    private final QuestionAnswerService questionAnswerService;

    @PostMapping("/in/{sessionId}/que")
    public ResponseEntity<QuestionAnswerResponse> addQuestion(
            @PathVariable UUID sessionId,
            @RequestParam String question,
            @RequestParam String difficulty
            ){
        QuestionAnswerResponse response= questionAnswerService
                .addQuestion(sessionId,question,difficulty);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/que/{questionId}/ans")
    public ResponseEntity<QuestionAnswerResponse> submitAnswer(
            @PathVariable UUID questionId,
            @RequestBody SubmitAnswerRequest request
            ) {
        QuestionAnswerResponse response = questionAnswerService.submitAnswer(questionId,request);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/in/{sessionId}/ans")
    public ResponseEntity<List<QuestionAnswerResponse>> getQuestions(
            @PathVariable UUID sessionId
    ) {
        List<QuestionAnswerResponse> response = questionAnswerService.getSessionQuestions(sessionId);

        return ResponseEntity.ok(response);
    }
}
