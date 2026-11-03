package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.ScorecardResponse;
import com.ai_interview_platform.Security.UserDetailsImp;
import com.ai_interview_platform.Services.ScorecardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/in")
@RequiredArgsConstructor
public class ScorecardController {

    private final ScorecardService scorecardService;

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<ScorecardResponse> completeInterview(
            @PathVariable UUID sessionId, @AuthenticationPrincipal UserDetailsImp user){
        ScorecardResponse response=scorecardService.completeInterview(sessionId,user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}/scorecard")
    public ResponseEntity<ScorecardResponse> getScoreCard(
            @PathVariable UUID sessionId,@AuthenticationPrincipal UserDetailsImp user){
        ScorecardResponse response=scorecardService.getScorecard(sessionId,user.getId());
        
        return ResponseEntity.ok(response);
    }
}
