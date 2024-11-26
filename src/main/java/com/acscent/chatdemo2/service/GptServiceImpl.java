package com.acscent.chatdemo2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.acscent.chatdemo2.dto.GptRequestDTO;
import com.acscent.chatdemo2.dto.GptResponseDTO;
import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.exceptions.GptProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {

    private final RestTemplate restTemplate;

    @Value("${api.key}")
    private String apiKey;

    @Override
    public String requestToGpt(List<Message> prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // model: "gpt-4-turbo-2024-04-09",
        GptRequestDTO requestDTO = GptRequestDTO.builder()
                .model("gpt-4o-2024-05-13")
                .messages(prompt)
                .build();

        HttpEntity<GptRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        try {
            GptResponseDTO response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    GptResponseDTO.class
            ).getBody();

            if (response != null && !response.getChoices().isEmpty()) {
                String result = response.getChoices().get(0).getMessage().getContent().trim();
                log.info(result);
                return result;
            } else {
                return "";
            }
        } catch (HttpClientErrorException e) {
            log.error("Error response from GPT: {}", e.getResponseBodyAsString());
            throw new GptProcessingException("Error response from GPT:\n" + e.getResponseBodyAsString());
        }
    }
}
