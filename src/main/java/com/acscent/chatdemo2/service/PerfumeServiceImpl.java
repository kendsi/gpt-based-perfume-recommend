package com.acscent.chatdemo2.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.data.Appearance;
import com.acscent.chatdemo2.data.ParsedGptResponse;
import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;
import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.exceptions.ImageNotFoundException;
import com.acscent.chatdemo2.exceptions.ImageSavingException;
import com.acscent.chatdemo2.model.MainNote;
import com.acscent.chatdemo2.model.Perfume;
import com.acscent.chatdemo2.repository.PerfumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final GptService gptService;
    private final GptResponseParser gptResponseParser;
    private final NoteService noteService;
    private final PerfumeRepository perfumeRepository;
    private final PromptService promptService;

    @Override
    public PerfumeResponseDTO createPerfume(PerfumeRequestDTO perfumeRequest) {

        log.info("Language: " + perfumeRequest.getLanguage());
        List<Message> prompt = promptService.loadPrompt(perfumeRequest.getLanguage());
        String notePrompt = noteService.getFilteredNotes(perfumeRequest.getPreferredScent(), perfumeRequest.getDislikedScent());
        log.info(notePrompt);

        List<Message> formattedPrompt = promptService.formatPrompt(perfumeRequest, prompt, notePrompt);

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
    public Resource getImage(String fileName) {
        String dirPath = System.getProperty("user.home") + "/images";

        try {
            Path filePath = Paths.get(dirPath, fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ImageNotFoundException("Image not found: " + dirPath + fileName);
            }
        } catch (Exception e) {
            throw new ImageNotFoundException("Image not found: " + dirPath + fileName);
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
        MainNote selectedNote = noteService.getSelectedNote(parsedGptResponse.getPerfumeName());
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
            .musk(selectedNote.getMusk())
            .fruity(selectedNote.getFruity())
            .spicy(selectedNote.getSpicy())
            .build();
    }

}
