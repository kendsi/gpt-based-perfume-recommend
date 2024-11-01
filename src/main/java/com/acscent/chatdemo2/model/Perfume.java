package com.acscent.chatdemo2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "perfume_result")
@Data
public class Perfume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "perfume_name", nullable = false)
    private String perfumeName;

    @Column(name = "insights", columnDefinition = "TEXT")
    private String insights;

    @Column(name = "top_note_id")
    private Long topNoteId;

    @Column(name = "middle_note_id")
    private Long middleNoteId;

    @Column(name = "base_note_id")
    private Long baseNoteId;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    public Perfume() {}

    @Builder
    public Perfume(Long id, String code, String userName, String perfumeName, String insights, Long topNoteId, Long middleNoteId, Long baseNoteId, String imageName) {
        this.id = id;
        this.code = code;
        this.userName = userName;
        this.perfumeName = perfumeName;
        this.insights = insights;
        this.topNoteId = topNoteId;
        this.middleNoteId = middleNoteId;
        this.baseNoteId = baseNoteId;
        this.imageName = imageName;
    }
}