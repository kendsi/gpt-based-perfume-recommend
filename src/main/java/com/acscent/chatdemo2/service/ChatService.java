package com.acscent.chatdemo2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.model.Chat;

import reactor.core.publisher.Mono;

@Service
public class ChatService {

    @Autowired
    private GptService gptService;

    @Autowired
    private GoogleService googleService;

    public Mono<Chat> getChatResponse(UserDTO userData) {
        
    }
}
