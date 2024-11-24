package com.acscent.chatdemo2.repository;

import com.acscent.chatdemo2.model.MainNote;
import java.util.List;
import java.util.Optional;

public interface MainNoteRepositoryCustom {
    List<MainNote> findByPreferredAndDislikedNotes(List<String> preferredNotes, List<String> dislikedNotes, String language);
    Optional<MainNote> findByPerfumeNameAndLanguage(String perfumeName, String language);
}