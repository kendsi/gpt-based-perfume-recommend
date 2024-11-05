package com.acscent.chatdemo2.repository;

import com.acscent.chatdemo2.model.MainNote;
import java.util.List;

public interface MainNoteRepositoryCustom {
    List<MainNote> findByPreferredAndDislikedNotes(List<String> preferredNotes, List<String> dislikedNotes);
}