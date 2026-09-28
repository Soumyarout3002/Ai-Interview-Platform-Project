package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.InterviewSessionResponse;
import com.ai_interview_platform.DTOs.StartInterviewRequest;
import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Services.InterviewSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/in")
@RequiredArgsConstructor
public class InterviewSessionController {

    private final InterviewSessionService interviewSessionService;

    @PostMapping
    public ResponseEntity<InterviewSessionResponse> startInterview(@RequestParam UUID userId, @RequestBody StartInterviewRequest request){
        InterviewSessionResponse response=interviewSessionService.startInterview(userId,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InterviewSessionResponse>>getInterviews(@RequestParam UUID userId){
        List<InterviewSessionResponse>responses=interviewSessionService.getUserInterview(userId);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<InterviewSessionResponse>getUserInterviews(@PathVariable UUID id){
        InterviewSessionResponse responses=interviewSessionService.getInterviewById(id);
        return ResponseEntity.ok(responses);
    }


}
