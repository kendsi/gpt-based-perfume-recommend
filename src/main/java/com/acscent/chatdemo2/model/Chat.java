package com.acscent.chatdemo2.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {

    private String topNote;
    private String middleNote;
    private String baseNote;
    
    private Image image;

    private User user;

    @Builder
    public Chat(String topNote, String middleNote, String baseNote, Image image, User user) {
        this.topNote = topNote;
        this.middleNote = middleNote;
        this.baseNote = baseNote;
        this.image = image;
        this.user = user;
    }
}
