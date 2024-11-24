package com.acscent.chatdemo2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acscent.chatdemo2.model.MainNote;

public interface MainNoteRepository extends JpaRepository<MainNote, Long>, MainNoteRepositoryCustom {
}
