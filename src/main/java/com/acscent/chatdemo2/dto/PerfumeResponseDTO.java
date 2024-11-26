package com.acscent.chatdemo2.dto;

import com.acscent.chatdemo2.data.Appearance;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfumeResponseDTO {
    
    private Long id;

    private String uuid;

    private String userName;
    private String perfumeName;

    private String mainNote;
    private String mainNoteDesc;
    private String mainNoteAnalysis;
    private String mainNoteImageUrl;
    private String middleNote;
    private String middleNoteDesc;
    private String middleNoteAnalysis;
    private String middleNoteImageUrl;
    private String baseNote;
    private String baseNoteDesc;
    private String baseNoteAnalysis;
    private String baseNoteImageUrl;

    // Appearance 분석 결과 리스트 (facialFeature, style, vibe)
    private Appearance appearance;
    private String profile;

    private String userImageUrl;

    private int citrus;
    private int floral;
    private int woody;
    private int musk;
    private int fruity;
    private int spicy;
}