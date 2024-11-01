package com.acscent.chatdemo2.service;

import java.util.List;

public interface NoteService {
    public String getFilteredNotesPrompt();
    public List<Long> updateNoteCount(List<String> selectedNotes);
}
