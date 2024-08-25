package com.acscent.chatdemo2.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Chat {

    private String perfumeName;
    private List<String> insights;
    private String topNote;
    private String middleNote;
    private String baseNote;
    private String summary;
    private String imagePath;

    private User user;
}
