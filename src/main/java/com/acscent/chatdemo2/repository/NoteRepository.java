package com.acscent.chatdemo2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.acscent.chatdemo2.model.Note;
import com.acscent.chatdemo2.model.Note.NoteType;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n WHERE n.noteType = :noteType AND n.count < (SELECT MIN(subN.count) FROM Note subN WHERE subN.noteType = :noteType) + 3")
    List<Note> findFilteredNotesByType(@Param("noteType") NoteType noteType);

    @Query("SELECT n FROM Note n WHERE n.name = :name AND n.noteType = :noteType")
    Optional<Note> findNoteByName(@Param("name") String name, @Param("noteType") NoteType noteType);

    @Modifying
    @Query("UPDATE Note n SET n.count = n.count + 1 WHERE n.id = :id AND n.noteType = :noteType")
    void updateNoteCount(@Param("id") Long id, @Param("noteType") NoteType noteType);
}