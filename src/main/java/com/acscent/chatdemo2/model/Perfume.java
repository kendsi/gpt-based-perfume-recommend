package com.acscent.chatdemo2.model;

import com.acscent.chatdemo2.data.Appearance;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "perfume_name", nullable = false)
    private String perfumeName;

    @ManyToOne
    @JoinColumn(name = "main_note_id", nullable = false)
    private MainNote mainNote;

    @Embedded
    private Appearance appearance;

    @Column(name = "profile", nullable = false, length = 5000)
    private String profile;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    public Perfume() {}

    @Builder
    public Perfume(Long id, String userName, MainNote mainNote, Appearance appearance, String profile, String imageUrl) {
        this.id = id;
        this.userName = userName;
        this.perfumeName = mainNote.getPerfumeName();
        this.mainNote = mainNote;
        this.appearance = appearance;
        this.profile = profile;
        this.imageUrl = imageUrl;
    }
}