package com.ai_interview_platform.Config;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Bean
    @Primary
    public Client geminiClient(
            @Value("${spring.ai.google.genai.api-key}") String apiKey,
            @Value("${gemini.timeout-ms:60000}") int timeoutMs,
            @Value("${gemini.max-attempts:1}") int maxAttempts) {

        return Client.builder()
                .apiKey(apiKey)
                .httpOptions(HttpOptions.builder()
                        .timeout(timeoutMs)
                        .retryOptions(HttpRetryOptions.builder()
                                .attempts(maxAttempts)
                                .build())
                        .build())
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder.build();
    }
}
