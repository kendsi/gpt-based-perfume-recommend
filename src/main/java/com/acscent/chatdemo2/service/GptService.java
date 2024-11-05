package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.GptRequestDTO.Message;

import java.util.List;

public interface GptService {
    String requestToGpt(List<Message> prompt);
}
