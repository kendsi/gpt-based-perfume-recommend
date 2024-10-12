package com.acscent.chatdemo2.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatResponseDTO {
    
    private String perfumeName;
    private String insights;
    private String topNote;
    private String middleNote;
    private String baseNote;

    public ChatResponseDTO() {}

    @Builder
    public ChatResponseDTO(String perfumeName, String insights, String topNote, String middleNote, String baseNote) {
        this.perfumeName = perfumeName;
        this.insights = insights;
        this.topNote = topNote;
        this.middleNote = middleNote;
        this.baseNote = baseNote;
    }
}