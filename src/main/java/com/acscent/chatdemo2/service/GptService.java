package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.GptRequestDTO.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GptService {
    CompletableFuture<String> requestToGpt(List<Message> prompt);
}
