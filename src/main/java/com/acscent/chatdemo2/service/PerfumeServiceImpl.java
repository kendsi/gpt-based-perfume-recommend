package com.acscent.chatdemo2.service;

import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.data.Appearance;
import com.acscent.chatdemo2.data.ParsedGptResponse;
import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;
import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.model.MainNote;
import com.acscent.chatdemo2.model.Perfume;
import com.acscent.chatdemo2.repository.PerfumeRepository;
import com.acscent.chatdemo2.util.GptResponseParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final GptService gptService;
    private final GptResponseParser gptResponseParser;
    private final NoteService noteService;
    private final PerfumeRepository perfumeRepository;
    private final PromptService promptService;
    private final S3Service s3Service;

    @Override
    public PerfumeResponseDTO createPerfume(PerfumeRequestDTO perfumeRequest) {

        log.info("Language: " + perfumeRequest.getLanguage());
        List<Message> prompt = promptService.loadPrompt(perfumeRequest.getLanguage());
        String notePrompt = noteService.getFilteredNotes(perfumeRequest.getPreference(), perfumeRequest.getLanguage());
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
        log.info("TOP NOTE ANALYSIS: " + parsedGptResponse.getTopNoteAnalysis());
        log.info("MIDDLE NOTE: " + parsedGptResponse.getMiddleNote());
        log.info("MIDDLE NOTE ANALYSIS: " + parsedGptResponse.getMiddleNoteAnalysis());
        log.info("BASE NOTE: " + parsedGptResponse.getBaseNote());
        log.info("BASE NOTE ANALYSIS: " + parsedGptResponse.getBaseNoteAnalysis());

        log.info("PROFILE");
        log.info(parsedGptResponse.getProfile());

        String imageUrl = s3Service.uploadImage(perfumeRequest.getImage(), perfumeRequest.getName());
        Perfume newPerfume = perfumeRepository.save(convertToModel(perfumeRequest, parsedGptResponse, imageUrl));

        return convertToDto(newPerfume);
    }
    
    private Perfume convertToModel(PerfumeRequestDTO perfumeRequest, ParsedGptResponse parsedGptResponse, String imageUrl) {
        MainNote selectedNote = noteService.getSelectedNote(parsedGptResponse.getTopNote());
        Appearance appearance = parsedGptResponse.getAppearance();
        return Perfume.builder()
            .userName(perfumeRequest.getName())
            .perfumeName(parsedGptResponse.getPerfumeName())
            .mainNote(selectedNote)
            .appearance(appearance)
            .topNoteAnalysis(parsedGptResponse.getTopNoteAnalysis())
            .middleNoteAnalysis(parsedGptResponse.getMiddleNoteAnalysis())
            .baseNoteAnalysis(parsedGptResponse.getBaseNoteAnalysis())
            .profile(parsedGptResponse.getProfile())
            .imageUrl(imageUrl)
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
            .mainNoteAnalysis(perfume.getTopNoteAnalysis())
            .mainNoteImageUrl(selectedNote.getImageUrl())
            .middleNote(selectedNote.getMiddleNote().getName())
            .middleNoteDesc(selectedNote.getMiddleNote().getScent())
            .middleNoteAnalysis(perfume.getMiddleNoteAnalysis())
            .middleNoteImageUrl(selectedNote.getMiddleNote().getImageUrl())
            .baseNote(selectedNote.getBaseNote().getName())
            .baseNoteDesc(selectedNote.getBaseNote().getScent())
            .baseNoteAnalysis(perfume.getBaseNoteAnalysis())
            .baseNoteImageUrl(selectedNote.getBaseNote().getImageUrl())
            .appearance(perfume.getAppearance())
            .profile(perfume.getProfile())
            .userImageUrl(perfume.getImageUrl())
            .citrus(selectedNote.getCitrus())
            .floral(selectedNote.getFloral())
            .woody(selectedNote.getWoody())
            .musk(selectedNote.getMusk())
            .fruity(selectedNote.getFruity())
            .spicy(selectedNote.getSpicy())
            .build();
    }

}
