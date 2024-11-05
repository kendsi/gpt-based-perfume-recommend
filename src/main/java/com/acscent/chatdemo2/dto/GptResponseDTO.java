package com.acscent.chatdemo2.dto;

import java.util.List;

import lombok.Data;

@Data
public class GptResponseDTO {
    private List<Choice> choices;

    public GptResponseDTO() {}

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String content;
    }
}