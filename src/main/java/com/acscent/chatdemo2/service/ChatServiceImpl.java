package com.acscent.chatdemo2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.dto.ChatResponseDTO;
import com.acscent.chatdemo2.dto.GptRequestDTO.Content;
import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.exceptions.CodeAlreadyUsedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final GptService gptService;
    private final GptResponseParser gptResponseParser;
    private final GoogleDriveService googleDriveService;
    private final GoogleSheetsService googleSheetsService;
    private final ObjectMapper objectMapper;

    @Value("classpath:prompts/korean_prompt.json")
    private Resource resource;

    @Override
    @Async
    public CompletableFuture<ChatResponseDTO> createPerfume(ChatRequestDTO chatData) {
        try {

            // Google Sheets에서 코드 확인
            CompletableFuture<String> findByCodeFuture = verifyCode(chatData.getCode());

            // 비동기적으로 이미지 업로드 실행
            CompletableFuture<Void> uploadImageFuture = googleDriveService.uploadImage(chatData.getImage());

            // 프롬프트 로드
            List<Message> prompt = loadPrompt();

            // GPT 요청을 비동기적으로 처리
            CompletableFuture<ChatResponseDTO> gptFuture = gptRequest(chatData, prompt);

            // 모든 비동기 작업 완료 후 ChatResponseDTO 반환
            return CompletableFuture.allOf(findByCodeFuture, uploadImageFuture)
                    .thenCompose(v -> gptFuture);

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private CompletableFuture<String> verifyCode(String code) {
        return googleSheetsService.findByCode(code)
            .thenApply(result -> {
                if ("FALSE".equals(result)) {
                    throw new CodeAlreadyUsedException(code);
                }
                return result;
            });
    }

    private List<Message> loadPrompt() throws IOException {
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Message>>() {});
    }

    private CompletableFuture<ChatResponseDTO> gptRequest(ChatRequestDTO chatData, List<Message> prompt) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return formatPrompt(chatData, prompt);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).thenCompose(formattedPrompt -> {
            CompletableFuture<String> gptResponseFuture = gptService.requestToGpt(formattedPrompt);
            return gptResponseFuture.thenApply(gptResponseParser::parseGptResponse);
        });
    }

    private List<Message> formatPrompt(ChatRequestDTO chatData, List<Message> prompt) throws IOException {
        String encodedImage = encodeImage(chatData.getImage());
    
        prompt.forEach(message -> {
            if (message.isContentString()) {
                String formattedContent = message.getContentAsString()
                        .replace("${userName}", chatData.getName())
                        .replace("${notesPrompt}", "Top Note: Rose, Middle Note: Jasmine, Base Note: Sandalwood")
                        .replace("${userGender}", chatData.getGender());
                message.setContent(formattedContent);
            } else if (message.isContentList()) {
                List<Content> contentList = objectMapper.convertValue(message.getContent(), new TypeReference<List<Content>>() {});
                contentList.forEach(item -> {
                    if ("text".equals(item.getType()) && item.getText() != null) {
                        String formattedText = item.getText()
                                .replace("${userName}", chatData.getName())
                                .replace("${notesPrompt}", "Top Note: Rose, Middle Note: Jasmine, Base Note: Sandalwood")
                                .replace("${userGender}", chatData.getGender());
                        item.setText(formattedText);
                    } else if ("image_url".equals(item.getType()) && item.getImageUrl() != null) {
                        String updatedUrl = item.getImageUrl().getUrl().replace("${encodedImage}", encodedImage);
                        item.getImageUrl().setUrl(updatedUrl);
                    }
                });
                message.setContent(contentList);
            }
        });
    
        return prompt;
    }

    private String encodeImage(MultipartFile image) throws IOException {
        return Base64.getEncoder().encodeToString(image.getBytes());
    }
}
