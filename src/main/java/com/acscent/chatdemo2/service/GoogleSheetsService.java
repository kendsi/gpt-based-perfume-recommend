package com.acscent.chatdemo2.service;

import java.util.concurrent.CompletableFuture;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;
import com.acscent.chatdemo2.dto.UserCodeResponseDTO;

public interface GoogleSheetsService {
    CompletableFuture<UserCodeResponseDTO> verifyCode(String code);
    CompletableFuture<String> getNotesPrompt();
    CompletableFuture<Void> updateCount(String selectedNote);
    CompletableFuture<Void> saveChatResponse(PerfumeResponseDTO chatResponse, PerfumeRequestDTO chatRequest);
}
