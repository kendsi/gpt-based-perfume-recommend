package com.acscent.chatdemo2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acscent.chatdemo2.exceptions.NoteNotFoundException;
import com.acscent.chatdemo2.model.Note;
import com.acscent.chatdemo2.model.Note.NoteType;
import com.acscent.chatdemo2.repository.NoteRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    public String getFilteredNotesPrompt() {
        // 필터링된 데이터 가져오기
        List<Note> topNotes = noteRepository.findFilteredNotesByType(NoteType.TOP);
        List<Note> middleNotes = noteRepository.findFilteredNotesByType(NoteType.MIDDLE);
        List<Note> baseNotes = noteRepository.findFilteredNotesByType(NoteType.BASE);

        // 최종 결과 문자열 생성
        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append("Top Note 향 오일 리스트:\n");
        topNotes.forEach(note -> resultBuilder.append(note.getName()).append("\n"));

        resultBuilder.append("\nMiddle Note 향 오일 리스트:\n");
        middleNotes.forEach(note -> resultBuilder.append(note.getName()).append("\n"));

        resultBuilder.append("\nBase Note 향 오일 리스트:\n");
        baseNotes.forEach(note -> resultBuilder.append(note.getName()).append("\n"));

        return resultBuilder.toString();
    }

    @Override
    @Transactional
    public List<Long> updateNoteCount(List<String> selectedNotes) {
        List<Long> updatedNoteIds = new ArrayList<>();

        for (int i = 0; i < selectedNotes.size(); i++) {
            String noteName = selectedNotes.get(i);
            Note note;
            switch (i) {
                case 0: // Top Note
                    note = noteRepository.findNoteByName(noteName, NoteType.TOP)
                            .orElseThrow(() -> new NoteNotFoundException("Top Note not found: " + noteName));
                    noteRepository.updateNoteCount(note.getId(), NoteType.TOP);
                    break;
                case 1: // Middle Note
                    note = noteRepository.findNoteByName(noteName, NoteType.MIDDLE)
                            .orElseThrow(() -> new NoteNotFoundException("Middle Note not found: " + noteName));
                    noteRepository.updateNoteCount(note.getId(), NoteType.MIDDLE);
                    break;
                case 2: // Base Note
                    note = noteRepository.findNoteByName(noteName, NoteType.BASE)
                            .orElseThrow(() -> new NoteNotFoundException("Base Note not found: " + noteName));
                    noteRepository.updateNoteCount(note.getId(), NoteType.BASE);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid note type index: " + i);
            }
            updatedNoteIds.add(note.getId());
        }
        return updatedNoteIds;
    }
}
