package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;

import java.util.concurrent.CompletableFuture;

public interface GoogleDriveService {
    CompletableFuture<Void> uploadImage(PerfumeRequestDTO chatRequest);
}