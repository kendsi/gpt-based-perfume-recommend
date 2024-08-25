package com.acscent.chatdemo2.service;

import java.util.concurrent.CompletableFuture;

public interface GoogleSheetsService {
    CompletableFuture<String> findByCode(String code);
}
