package com.acscent.chatdemo2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NoteType noteType;

    public enum NoteType {
        TOP, MIDDLE, BASE
    }

    private String name;
    private int count;
    private String description;
    private String recommendation;

    public Note() {}

    @Builder
    public Note(Long id, NoteType noteType, String name, int count, String description, String recommendation) {
        this.id = id;
        this.noteType = noteType;
        this.name = name;
        this.count = count;
        this.description = description;
        this.recommendation = recommendation;
    }
}