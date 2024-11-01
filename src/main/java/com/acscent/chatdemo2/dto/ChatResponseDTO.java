package com.acscent.chatdemo2.dto;

import com.google.auto.value.AutoValue.Builder;

import lombok.Data;

@Data
@Builder
public class ChatResponseDTO {
    
    private String perfumeName;
    private String insights;
    private String topNote;
    private String middleNote;
    private String baseNote;
}

