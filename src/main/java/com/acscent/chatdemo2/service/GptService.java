package com.acscent.chatdemo2.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.dto.ChatResponseDTO;
import com.google.gson.Gson;

import reactor.core.publisher.Mono;

@Service
public class GptService {
    
    @Value("${api.key}")
    private static String API_KEY;

    private final WebClient webClient;

    public GptService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    public Mono<ChatResponseDTO> getChatResponse(ChatRequestDTO chatRequestDTO) {

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .bodyValue(chatRequestDTO)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class);
    }

    public ChatRequestDTO loadPrompt() throws IOException {
        Gson gson = new Gson();
        Resource resource = new ClassPathResource("prompts.json");
        InputStreamReader reader = new InputStreamReader(resource.getInputStream());
        return gson.fromJson(reader, ChatRequestDTO.class);
    }
}
