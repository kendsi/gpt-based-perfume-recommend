package com.acscent.chatdemo2.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParsedGptResponse {
    private String perfumeName;
    private String topNote;
    private String middleNote;
    private String baseNote;
    private Appearance appearance;
    private String profile;
}
