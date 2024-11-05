package com.acscent.chatdemo2.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.dto.GptRequestDTO.Content;
import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.exceptions.ImageEncodingException;
import com.acscent.chatdemo2.exceptions.InvalidLanguageInputException;
import com.acscent.chatdemo2.exceptions.PromptLoadingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {

    private final ObjectMapper objectMapper;
    
    @Value("file:${user.home}/prompts/korean_prompt.json")
    private Resource koreanPrompt;

    @Value("file:${user.home}/prompts/english_prompt.json")
    private Resource englishPrompt;

    @Value("file:${user.home}/prompts/japanese_prompt.json")
    private Resource japanesePrompt;

    @Value("file:${user.home}/prompts/chinese_prompt.json")
    private Resource chinesePrompt;

    @Override
    public List<Message> loadPrompt(String language) {
        try {
            if (language.equals("kor")) {
                return objectMapper.readValue(koreanPrompt.getInputStream(), new TypeReference<List<Message>>() {});
            } else if (language.equals("eng")) {
                return objectMapper.readValue(englishPrompt.getInputStream(), new TypeReference<List<Message>>() {});
            } else if (language.equals("jp")) {
                return objectMapper.readValue(japanesePrompt.getInputStream(), new TypeReference<List<Message>>() {});
            } else if (language.equals("cn")) {
                return objectMapper.readValue(chinesePrompt.getInputStream(), new TypeReference<List<Message>>() {});
            }
        } catch (IOException e) {
            throw new PromptLoadingException("Cannot Load Prompt for language: " + language);
        }
        throw new InvalidLanguageInputException("Invalid Language Input: " + language);
    }

    @Override
    public List<Message> formatPrompt(PerfumeRequestDTO perfumeRequest, List<Message> prompt, String notesPrompt) {
        String encodedImage = encodeImage(perfumeRequest.getImage());
    
        prompt.forEach(message -> {
            if (message.isContentString()) {
                String formattedContent = message.getContentAsString()
                        .replace("${userName}", perfumeRequest.getName())
                        .replace("${notesPrompt}", notesPrompt)
                        .replace("${userGender}", perfumeRequest.getGender())
                        .replace("${keyword}", perfumeRequest.getKeyword());
                message.setContent(formattedContent);
            } else if (message.isContentList()) {
                List<Content> contentList = objectMapper.convertValue(message.getContent(), new TypeReference<List<Content>>() {});
                contentList.forEach(item -> {
                    if ("text".equals(item.getType()) && item.getText() != null) {
                        String formattedText = item.getText()
                                .replace("${userName}", perfumeRequest.getName())
                                .replace("${notesPrompt}", notesPrompt)
                                .replace("${userGender}", perfumeRequest.getGender())
                                .replace("${keyword}", perfumeRequest.getKeyword());
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

    private String encodeImage(MultipartFile image) {
        try {
            return Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            throw new ImageEncodingException("Failed to encode image");
        }
    }
}
