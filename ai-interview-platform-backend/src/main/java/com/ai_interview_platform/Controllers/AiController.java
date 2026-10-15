package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.Services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    public ResponseEntity<String>generateQuestion(@RequestParam String category ,
                                                  @RequestParam String difficult){
        String question = aiService.generateQuestion(category,difficult);

        return ResponseEntity.ok(question);
    }

}
