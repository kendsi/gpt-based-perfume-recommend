package com.acscent.chatdemo2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatResponseDTO {
    
    private String perfumeName;
    private String insights;
    private String topNote;
    private String middleNote;
    private String baseNote;
}