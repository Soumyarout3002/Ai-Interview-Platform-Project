package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.InterviewSessionResponse;
import com.ai_interview_platform.DTOs.StartInterviewRequest;
import com.ai_interview_platform.Models.InterviewSession;
import com.ai_interview_platform.Security.UserDetailsImp;
import com.ai_interview_platform.Services.InterviewSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/in")
@RequiredArgsConstructor
public class InterviewSessionController {

    private final InterviewSessionService interviewSessionService;

    @PostMapping
    public ResponseEntity<InterviewSessionResponse> startInterview(@AuthenticationPrincipal UserDetailsImp userId, @RequestBody StartInterviewRequest request){
        InterviewSessionResponse response=interviewSessionService.startInterview(userId.getId(),request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InterviewSessionResponse>>getInterviews(@RequestParam UUID userId, @AuthenticationPrincipal UserDetailsImp user){
        List<InterviewSessionResponse>responses=interviewSessionService.getUserInterview(user.getId());
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<InterviewSessionResponse>getUserInterviews(@PathVariable UUID id,@AuthenticationPrincipal UserDetailsImp user){
        InterviewSessionResponse responses=interviewSessionService.getInterviewById(id,user.getId());
        return ResponseEntity.ok(responses);
    }


}
