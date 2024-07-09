package com.acscent.chatdemo2.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDTO {

    private String model;
    private List<Message> messages;

    public ChatRequestDTO(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }
}