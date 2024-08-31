package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.dto.ChatResponseDTO;

import java.util.concurrent.CompletableFuture;

public interface ChatService {
    CompletableFuture<ChatResponseDTO> createPerfume(ChatRequestDTO chatRequest);
}
