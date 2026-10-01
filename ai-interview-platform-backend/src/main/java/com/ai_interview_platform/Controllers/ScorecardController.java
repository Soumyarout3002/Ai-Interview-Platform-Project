package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.CompleteInterviewRequest;
import com.ai_interview_platform.DTOs.ScorecardResponse;
import com.ai_interview_platform.Services.ScorecardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/in")
@RequiredArgsConstructor
public class ScorecardController {

    private final ScorecardService scorecardService;

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<ScorecardResponse> completeInterview(
            @PathVariable UUID sessionId,
            @RequestBody CompleteInterviewRequest request
            ){
        ScorecardResponse response=scorecardService.completeInterview(sessionId,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}/scorecard")
    public ResponseEntity<ScorecardResponse> completeInterview(
            @PathVariable UUID sessionId
    ){
        ScorecardResponse response=scorecardService.getScorecard(sessionId);
        
        return ResponseEntity.ok(response);
    }
}
