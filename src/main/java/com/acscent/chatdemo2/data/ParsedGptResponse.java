package com.acscent.chatdemo2.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParsedGptResponse {
    private String perfumeName;
    private String topNote;
    private String topNoteAnalysis;
    private String middleNote;
    private String middleNoteAnalysis;
    private String baseNote;
    private String baseNoteAnalysis;
    private Appearance appearance;
    private String profile;
}
