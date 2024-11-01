package com.acscent.chatdemo2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfumeResponseDTO {
    
    private Long id;
    private String userName;
    private String perfumeName;
    private String insights;
    private String topNote;
    private String middleNote;
    private String baseNote;
    private String imageName;
}