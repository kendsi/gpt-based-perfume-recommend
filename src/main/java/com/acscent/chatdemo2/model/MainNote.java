package com.acscent.chatdemo2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "main_note")
public class MainNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String scent;
    private String perfumeName;

    @Column(length = 500)
    private String description;
    @Column(length = 500)
    private String recommendation;
    
    private int citrus;
    private int floral;
    private int woody;
    private int watery;
    private int fruity;
    private int spicy;

    @OneToOne
    @JoinColumn(name = "middle_note_id")
    private SubNote middleNote;

    @OneToOne
    @JoinColumn(name = "base_note_id")
    private SubNote baseNote;

    public MainNote() {}

    @Builder
    public MainNote(Long id, String name, String scent, String description, String recommendation, String perfumeName, int citrus, int floral, int woody, int watery, int fruity, int spicy, SubNote middleNote, SubNote baseNote) {
        this.id = id;
        this.name = name;
        this.scent = scent;
        this.description = description;
        this.perfumeName = perfumeName;
        this.recommendation = recommendation;
        this.citrus = citrus;
        this.floral = floral;
        this.woody = woody;
        this.watery = watery;
        this.fruity = fruity;
        this.spicy = spicy;
        this.middleNote = middleNote;
        this.baseNote = baseNote;
    }
}