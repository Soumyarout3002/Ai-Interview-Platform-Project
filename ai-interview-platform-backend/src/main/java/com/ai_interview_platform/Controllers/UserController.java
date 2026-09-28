package com.ai_interview_platform.Controllers;

import com.ai_interview_platform.DTOs.CreateUserRequest;
import com.ai_interview_platform.Models.User;
import com.ai_interview_platform.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request){
        User user=User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
}
