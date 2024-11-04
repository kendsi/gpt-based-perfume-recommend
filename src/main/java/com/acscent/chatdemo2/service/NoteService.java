package com.acscent.chatdemo2.service;

import java.util.List;

import com.acscent.chatdemo2.model.MainNote;

public interface NoteService {
    public String getFilteredNotes(List<String> preferred, List<String> disliked);
    public MainNote getSelectedNote(String noteName);
}
