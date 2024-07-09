package com.acscent.chatdemo2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponseDTO {
    private Choice[] choices;

    public static class Choice {
        private Message message;
        private int index;
    }
}