package com.acscent.chatdemo2.service;

import java.util.concurrent.CompletableFuture;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.dto.ChatResponseDTO;
import com.acscent.chatdemo2.dto.PassCodeResponseDTO;

public interface GoogleSheetsService {
    CompletableFuture<PassCodeResponseDTO> verifyCode(String code);
    CompletableFuture<String> getNotesPrompt();
    CompletableFuture<Void> updateCount(String selectedNote);
    CompletableFuture<Void> saveChatResponse(ChatResponseDTO chatResponse, ChatRequestDTO chatRequest);
}
