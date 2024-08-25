package com.acscent.chatdemo2.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GptResponseDTO {
    private List<Choice> choices;

    public GptResponseDTO() {}

    @Getter
    @Setter
    public static class Choice {
        private Message message;
    }

    @Getter
    @Setter
    public static class Message {
        private String content;
    }
}
