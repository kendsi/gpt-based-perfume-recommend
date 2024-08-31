package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.ChatRequestDTO;

import java.util.concurrent.CompletableFuture;

public interface GoogleDriveService {
    CompletableFuture<Void> uploadImage(ChatRequestDTO chatRequest);
}