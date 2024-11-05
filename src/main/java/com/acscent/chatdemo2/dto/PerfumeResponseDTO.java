package com.acscent.chatdemo2.dto;

import com.acscent.chatdemo2.data.Appearance;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfumeResponseDTO {
    
    private Long id;

    private String userName;
    private String perfumeName;

    private String mainNote;
    private String mainNoteDesc;
    private String middleNote;
    private String middleNoteDesc;
    private String baseNote;
    private String baseNoteDesc;

    // Appearance 분석 결과 리스트 (facialFeature, style, vibe)
    private Appearance appearance;

    private String profile;

    private String imageName;

    private int citrus;
    private int floral;
    private int woody;
    private int musk;
    private int fruity;
    private int spicy;
}