package com.acscent.chatdemo2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;
import com.acscent.chatdemo2.dto.GptRequestDTO.Content;
import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.exceptions.InvalidLanguageInputException;
import com.acscent.chatdemo2.exceptions.ImageEncodingException;
import com.acscent.chatdemo2.exceptions.ImageSavingException;
import com.acscent.chatdemo2.exceptions.PromptLoadingException;
import com.acscent.chatdemo2.model.Perfume;
import com.acscent.chatdemo2.repository.PerfumeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final GptService gptService;
    private final GptResponseParser gptResponseParser;
    // private final GoogleDriveService googleDriveService;
    // private final GoogleSheetsService googleSheetsService;
    private final NoteService noteService;
    private final PerfumeRepository perfumeRepository;
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
    public PerfumeResponseDTO createPerfume(PerfumeRequestDTO perfumeRequest) {

        List<Message> prompt = loadPrompt(perfumeRequest.getLanguage());
        String notePrompt = noteService.getFilteredNotesPrompt();
        List<Message> formattedPrompt = formatPrompt(perfumeRequest, prompt, notePrompt);

        log.info(notePrompt);

        PerfumeResponseDTO chatResponse = gptResponseParser.parseGptResponse(gptService.requestToGpt(formattedPrompt));
        log.info(chatResponse.getTopNote() + chatResponse.getMiddleNote() + chatResponse.getBaseNote());
        List<String> selectedNotes = extractSelectedNotes(chatResponse);
        List<Long> noteIds = noteService.updateNoteCount(selectedNotes);

        String imageName = saveImage(perfumeRequest.getImage(), perfumeRequest.getName());

        Perfume perfume = Perfume.builder()
                        .code(perfumeRequest.getCode())
                        .userName(perfumeRequest.getName())
                        .perfumeName(chatResponse.getPerfumeName())
                        .insights(chatResponse.getInsights())
                        .topNoteId(noteIds.get(0))
                        .middleNoteId(noteIds.get(1))
                        .baseNoteId(noteIds.get(2))
                        .imageName(imageName)
                        .build();

        Perfume newPerfume = perfumeRepository.save(perfume);

        chatResponse.setId(newPerfume.getId());
        chatResponse.setUserName(perfumeRequest.getName());
        chatResponse.setImageName(imageName);

        return chatResponse;
    }

    private List<Message> loadPrompt(String language) {
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

    private List<Message> formatPrompt(PerfumeRequestDTO perfumeRequest, List<Message> prompt, String notesPrompt) {
        String encodedImage = encodeImage(perfumeRequest.getImage());
    
        prompt.forEach(message -> {
            if (message.isContentString()) {
                String formattedContent = message.getContentAsString()
                        .replace("${userName}", perfumeRequest.getName())
                        .replace("${notesPrompt}", notesPrompt)
                        .replace("${userGender}", perfumeRequest.getGender());
                message.setContent(formattedContent);
            } else if (message.isContentList()) {
                List<Content> contentList = objectMapper.convertValue(message.getContent(), new TypeReference<List<Content>>() {});
                contentList.forEach(item -> {
                    if ("text".equals(item.getType()) && item.getText() != null) {
                        String formattedText = item.getText()
                                .replace("${userName}", perfumeRequest.getName())
                                .replace("${notesPrompt}", notesPrompt)
                                .replace("${userGender}", perfumeRequest.getGender());
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

    private List<String> extractSelectedNotes(PerfumeResponseDTO chatResponse) {
        // GPT 응답에서 Top, Middle, Base 노트의 이름을 추출하는 로직
        List<String> selectedNotes = new ArrayList<>();

        String topNote = parseNoteName(chatResponse.getTopNote());
        String middleNote = parseNoteName(chatResponse.getMiddleNote());
        String baseNote = parseNoteName(chatResponse.getBaseNote());

        log.info("Extracted Top Note: {}", topNote);
        log.info("Extracted Middle Note: {}", middleNote);
        log.info("Extracted Base Note: {}", baseNote);
        
        selectedNotes.add(topNote);
        selectedNotes.add(middleNote);
        selectedNotes.add(baseNote);
    
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

    private String saveImage(MultipartFile image, String userName) {

        String fileName = null;

        if (image != null && !image.isEmpty()) {
            
            String fileExtension = image.getOriginalFilename();
            if (fileExtension != null && fileExtension.contains(".")) {
                // 마지막 점(.) 이후의 문자열을 추출하여 확장자로 사용
                fileExtension = fileExtension.substring(fileExtension.lastIndexOf("."));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String formattedDateTime = LocalDateTime.now().format(formatter);
            fileName = formattedDateTime + "-" + userName + fileExtension;

            String userHome = System.getProperty("user.home");
            String dirPath = userHome + "/images";

            try {
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filePath = dirPath + "/" + fileName;
                image.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new ImageSavingException("Failed to save image file");
            }

        }

        return fileName;
    }
}
