package com.acscent.chatdemo2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "sub_note")
public class SubNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String scent;

    private String imageUrl;

    public SubNote() {}

    @Builder
    public SubNote(Long id, String name, String scent, String imageUrl) {
        this.id = id;
        this.name = name;
        this.scent = scent;
        this.imageUrl = imageUrl;
    }
}
