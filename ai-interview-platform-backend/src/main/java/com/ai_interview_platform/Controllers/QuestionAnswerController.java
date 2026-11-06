package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.QuestionAnswerResponse;
import com.ai_interview_platform.DTOs.SubmitAnswerRequest;
import com.ai_interview_platform.Security.UserDetailsImp;
import com.ai_interview_platform.Services.QuestionAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
            @AuthenticationPrincipal UserDetailsImp userId,
            @RequestParam String question,
            @RequestParam String difficulty
            ){
        QuestionAnswerResponse response= questionAnswerService
                .addQuestion(sessionId,userId.getId(),question,difficulty);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/in/{sessionId}/que/generate")
    public ResponseEntity<QuestionAnswerResponse> generateQuestion(
            @PathVariable UUID sessionId,@AuthenticationPrincipal UserDetailsImp userId){
        QuestionAnswerResponse response= questionAnswerService
                .generateQuestion(sessionId,userId.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/in/{sessionId}/que/generate/stream",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateQuestionStream(
            @PathVariable UUID sessionId,@AuthenticationPrincipal UserDetailsImp userId){

        return questionAnswerService.generateQuestionStream(
                sessionId,
                userId.getId()
        );
    }

    @PostMapping("/que/{questionId}/ans")
    public ResponseEntity<QuestionAnswerResponse> submitAnswer(
            @PathVariable UUID questionId,
            @AuthenticationPrincipal UserDetailsImp userId,
            @RequestBody SubmitAnswerRequest request
            ) {
        QuestionAnswerResponse response = questionAnswerService.submitAnswer(questionId,userId.getId(),request);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/in/{sessionId}/ans")
    public ResponseEntity<List<QuestionAnswerResponse>> getQuestions(
            @PathVariable UUID sessionId,@AuthenticationPrincipal UserDetailsImp userId
    ) {
        List<QuestionAnswerResponse> response = questionAnswerService.getSessionQuestions(sessionId,userId.getId());

        return ResponseEntity.ok(response);
    }
}
