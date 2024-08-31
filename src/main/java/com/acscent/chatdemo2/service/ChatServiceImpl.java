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
import com.acscent.chatdemo2.exceptions.InvalidLanguageInputException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
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
    private Resource koreanPrompt;
    
    @Value("classpath:prompts/english_prompt.json")
    private Resource englishPrompt;

    @Value("classpath:prompts/japanese_prompt.json")
    private Resource japanesePrompt;
    
    @Value("classpath:prompts/chinese_prompt.json")
    private Resource chinesePrompt;

    @Override
    @Async
    public CompletableFuture<ChatResponseDTO> createPerfume(ChatRequestDTO chatRequest) {
        try {
            // Google Sheets에서 코드 확인
            CompletableFuture<String> findByCodeFuture = googleSheetsService.verifyCode(chatRequest.getCode());

            // 비동기적으로 이미지 업로드 실행
            CompletableFuture<Void> uploadImageFuture = googleDriveService.uploadImage(chatRequest);

            // Google Sheets에서 notesPrompt 데이터를 비동기적으로 가져옴
            CompletableFuture<String> notesPromptFuture = googleSheetsService.getNotesPrompt();

            // 프롬프트 로드
            List<Message> prompt = loadPrompt(chatRequest.getLanguage());

            // 모든 비동기 작업 완료 후 GPT 요청을 비동기적으로 처리
            return CompletableFuture.allOf(findByCodeFuture, uploadImageFuture, notesPromptFuture)
                .thenComposeAsync(v -> notesPromptFuture.thenComposeAsync(notesPrompt -> gptRequest(chatRequest, prompt, notesPrompt)))
                    .thenComposeAsync(chatResponse -> {
                        // GPT 응답에서 선택된 노트를 추출
                        List<String> selectedNotes = extractSelectedNotes(chatResponse);

                        // 선택된 노트들의 count를 업데이트하는 비동기 작업 실행
                        CompletableFuture<Void> updateCountFuture = updateNoteCounts(selectedNotes);

                        // GPT 응답 결과를 저장하는 비동기 작업 실행
                        CompletableFuture<Void> saveResponseFuture = googleSheetsService.saveChatResponse(chatResponse, chatRequest);

                        // 모든 작업이 완료되면 최종 응답 반환
                        return CompletableFuture.allOf(updateCountFuture, saveResponseFuture)
                                .thenApplyAsync(v -> chatResponse);
                    }
                );

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    private CompletableFuture<ChatResponseDTO> gptRequest(ChatRequestDTO chatRequest, List<Message> prompt, String notesPrompt) {
        try {
            // prompt를 포맷하는 부분은 비동기 메서드 내에서 직접 실행됩니다.
            List<Message> formattedPrompt = formatPrompt(chatRequest, prompt, notesPrompt);

            // GPT 요청을 비동기적으로 실행합니다.
            CompletableFuture<String> gptResponseFuture = gptService.requestToGpt(formattedPrompt);

            return gptResponseFuture.thenApply(gptResponse -> {
                log.info("GPT Response: {}", gptResponse);
                return gptResponseParser.parseGptResponse(gptResponse);
            });
        } catch (IOException e) {
            return CompletableFuture.failedFuture(new RuntimeException(e));
        }
    }

    @Async
    private CompletableFuture<Void> updateNoteCounts(List<String> selectedNotes) {
        // 노트 유형에 따라 범위를 지정하고 업데이트 실행
        List<CompletableFuture<Void>> updateFutures = selectedNotes.stream()
            .map(note -> googleSheetsService.updateCount(note))
            .collect(Collectors.toList());

        // 모든 업데이트 작업이 완료될 때까지 기다림
        return CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0]));
    }

    private List<Message> loadPrompt(String language) throws IOException {
        if (language.equals("kor")) {
            return objectMapper.readValue(koreanPrompt.getInputStream(), new TypeReference<List<Message>>() {});
        }
        else if (language.equals("eng")) {
            return objectMapper.readValue(englishPrompt.getInputStream(), new TypeReference<List<Message>>() {});
        }
        else if (language.equals("jp")) {
            return objectMapper.readValue(japanesePrompt.getInputStream(), new TypeReference<List<Message>>() {});
        }
        else if (language.equals("cn")) {
            return objectMapper.readValue(chinesePrompt.getInputStream(), new TypeReference<List<Message>>() {});
        }
        throw new InvalidLanguageInputException("Invalid Language Input: " + language);
    }

    private List<Message> formatPrompt(ChatRequestDTO chatRequest, List<Message> prompt, String notesPrompt) throws IOException {
        String encodedImage = encodeImage(chatRequest.getImage());
    
        prompt.forEach(message -> {
            if (message.isContentString()) {
                String formattedContent = message.getContentAsString()
                        .replace("${userName}", chatRequest.getName())
                        .replace("${notesPrompt}", notesPrompt)
                        .replace("${userGender}", chatRequest.getGender());
                message.setContent(formattedContent);
            } else if (message.isContentList()) {
                List<Content> contentList = objectMapper.convertValue(message.getContent(), new TypeReference<List<Content>>() {});
                contentList.forEach(item -> {
                    if ("text".equals(item.getType()) && item.getText() != null) {
                        String formattedText = item.getText()
                                .replace("${userName}", chatRequest.getName())
                                .replace("${notesPrompt}", notesPrompt)
                                .replace("${userGender}", chatRequest.getGender());
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

    private List<String> extractSelectedNotes(ChatResponseDTO chatResponse) {
        // GPT 응답에서 Top, Middle, Base 노트의 이름을 추출하는 로직
        List<String> selectedNotes = new ArrayList<>();
        
        // 예시로 topNote, middleNote, baseNote를 GPT 응답에서 파싱하여 리스트에 추가
        selectedNotes.add(parseNoteName(chatResponse.getTopNote()));    // 예시: "AC'SCENT 06 그린 망고, 연꽃"
        selectedNotes.add(parseNoteName(chatResponse.getMiddleNote())); // 예시: "AC'SCENT 12 백합, 목련"
        selectedNotes.add(parseNoteName(chatResponse.getBaseNote()));   // 예시: "AC'SCENT 27 차이니즈 페퍼, 샌달우드"
    
        return selectedNotes;
    }

    private String parseNoteName(String note) {
        // 노트 문자열에서 노트 이름 부분을 추출하는 로직
        // 예: "AC'SCENT 06 그린 망고, 연꽃 | ..."에서 "AC'SCENT 06 그린 망고, 연꽃"만 추출
        if (note != null && note.contains("|")) {
            return note.split("\\|")[0].trim();
        }
        return note;
    }
}
