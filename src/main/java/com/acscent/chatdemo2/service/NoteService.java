package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.data.Preference;
import com.acscent.chatdemo2.model.MainNote;

public interface NoteService {
    public String getFilteredNotes(Preference preference);
    public MainNote getSelectedNote(String noteName);
}