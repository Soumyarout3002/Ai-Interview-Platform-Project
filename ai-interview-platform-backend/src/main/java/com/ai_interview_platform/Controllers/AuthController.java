package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.AuthResponse;
import com.ai_interview_platform.DTOs.CreateUserRequest;
import com.ai_interview_platform.DTOs.LoginRequest;
import com.ai_interview_platform.DTOs.UserResponse;
import com.ai_interview_platform.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reg")
    public ResponseEntity<UserResponse>register(@RequestBody CreateUserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse>login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
