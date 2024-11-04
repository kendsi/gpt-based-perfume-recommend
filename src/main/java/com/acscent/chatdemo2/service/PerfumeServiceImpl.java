package com.acscent.chatdemo2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.data.Appearance;
import com.acscent.chatdemo2.data.ParsedGptResponse;
import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;
import com.acscent.chatdemo2.dto.GptRequestDTO.Content;
import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.exceptions.InvalidLanguageInputException;
import com.acscent.chatdemo2.exceptions.ImageEncodingException;
import com.acscent.chatdemo2.exceptions.ImageSavingException;
import com.acscent.chatdemo2.exceptions.PromptLoadingException;
import com.acscent.chatdemo2.model.MainNote;
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
import java.util.stream.Collectors;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final GptService gptService;
    private final GptResponseParser gptResponseParser;
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

        log.info("Language: " + perfumeRequest.getLanguage());
        List<Message> prompt = loadPrompt(perfumeRequest.getLanguage());
        String notePrompt = noteService.getFilteredNotes(perfumeRequest.getPreferred(), perfumeRequest.getDisliked());
        log.info(notePrompt);

        List<Message> formattedPrompt = formatPrompt(perfumeRequest, prompt, notePrompt);

        ParsedGptResponse parsedGptResponse = gptResponseParser.parseGptResponse(gptService.requestToGpt(formattedPrompt));

        log.info("RECOMMENDED PERFUME: " + parsedGptResponse.getPerfumeName());

        log.info("APPEARANCE ANALYSIS");
        log.info("FACIAL FEATURE: " + parsedGptResponse.getAppearance().getFacialFeature());
        log.info("STYLE: " + parsedGptResponse.getAppearance().getStyle());
        log.info("VIBE: " + parsedGptResponse.getAppearance().getVibe());

        log.info("NOTES");
        log.info("TOP NOTE: " + parsedGptResponse.getTopNote());
        log.info("MIDDLE NOTE: " + parsedGptResponse.getMiddleNote());
        log.info("BASE NOTE: " + parsedGptResponse.getBaseNote());

        log.info("PROFILE");
        log.info(parsedGptResponse.getProfile());

        String imageName = saveImage(perfumeRequest.getImage(), perfumeRequest.getName());
        Perfume newPerfume = perfumeRepository.save(convertToModel(perfumeRequest, parsedGptResponse, imageName));

        return convertToDto(newPerfume);
    }

    @Override
    public List<PerfumeResponseDTO> getAllPerfumeResults() {
        List<Perfume> perfumeList = perfumeRepository.findAll();
        return perfumeList.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
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

    private Perfume convertToModel(PerfumeRequestDTO perfumeRequest, ParsedGptResponse parsedGptResponse, String imageName) {
        MainNote selectedNote = noteService.getSelectedNote(parsedGptResponse.getTopNote());
        Appearance appearance = parsedGptResponse.getAppearance();
        return Perfume.builder()
            .code(perfumeRequest.getCode())
            .userName(perfumeRequest.getName())
            .mainNote(selectedNote)
            .appearance(appearance)
            .profile(parsedGptResponse.getProfile())
            .imageName(imageName)
            .build();
    }
    
    private PerfumeResponseDTO convertToDto(Perfume perfume) {
        MainNote selectedNote = perfume.getMainNote();

        return PerfumeResponseDTO.builder()
            .id(perfume.getId())
            .userName(perfume.getUserName())
            .perfumeName(selectedNote.getPerfumeName())
            .mainNote(selectedNote.getName())
            .mainNoteDesc(selectedNote.getScent())
            .middleNote(selectedNote.getMiddleNote().getName())
            .middleNoteDesc(selectedNote.getMiddleNote().getScent())
            .baseNote(selectedNote.getBaseNote().getName())
            .baseNoteDesc(selectedNote.getBaseNote().getScent())
            .appearance(perfume.getAppearance())
            .profile(perfume.getProfile())
            .imageName(perfume.getImageName())
            .citrus(selectedNote.getCitrus())
            .floral(selectedNote.getFloral())
            .woody(selectedNote.getWoody())
            .watery(selectedNote.getWatery())
            .fruity(selectedNote.getFruity())
            .spicy(selectedNote.getSpicy())
            .build();
    }

}
