package com.acscent.chatdemo2.service;

import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.data.Preference;
import com.acscent.chatdemo2.data.Scent;
import com.acscent.chatdemo2.exceptions.NoteNotFoundException;
import com.acscent.chatdemo2.model.MainNote;
import com.acscent.chatdemo2.repository.MainNoteRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final MainNoteRepository mainNoteRepository;

    @Override
    public String getFilteredNotes(Preference preference, String language) {

        List<String> preferred = preference.getPreferred().stream()
                                          .map(Scent::getLabel)
                                          .collect(Collectors.toList());

        List<String> disliked = preference.getDisliked().stream()
                                                .map(Scent::getLabel)
                                                .collect(Collectors.toList());

        List<MainNote> filteredMainNotes = mainNoteRepository.findByPreferredAndDislikedNotes(preferred, disliked, language);

        if (filteredMainNotes.isEmpty() || filteredMainNotes == null) {
            throw new NoteNotFoundException("No notes match the specified criteria.");
        }

        // StringBuilder를 사용해 문자열을 생성
        StringBuilder result = new StringBuilder("The List Of Recommended Notes Set:\n");
        filteredMainNotes.forEach(note -> {
            result
                .append("Perfume: ")
                .append(note.getPerfumeName())
                .append("\n    Top Note: ")
                .append(note.getName())
                .append(", Top Note Description: ")
                .append(note.getScent())
                .append("\n    Middle Note: ")
                .append(note.getMiddleNote().getName())
                .append(", Middle Note Description: ")
                .append(note.getMiddleNote().getScent())
                .append("\n    Base Note: ")
                .append(note.getBaseNote().getName())
                .append(", Base Note Description: ")
                .append(note.getBaseNote().getScent())
                .append("\n    Perfume Description: ")
                .append(note.getDescription())
                .append("\n    Recommendation: ")
                .append(note.getRecommendation())
                .append("\n\n");
        });

        // 최종 문자열 반환
        return result.toString();
    }

    @Override
    public MainNote getSelectedNote(String perfumeName, String language) {

        MainNote selectedNote = mainNoteRepository.findByPerfumeNameAndLanguage(perfumeName, language)
                            .orElseThrow(() -> new NoteNotFoundException("Note not found with main note name: " + perfumeName));

        return selectedNote;
    }
}
