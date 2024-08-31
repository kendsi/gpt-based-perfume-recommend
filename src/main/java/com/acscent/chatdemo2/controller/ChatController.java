package com.acscent.chatdemo2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.dto.ChatResponseDTO;
import com.acscent.chatdemo2.service.ChatService;
import com.acscent.chatdemo2.service.ImWebService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final ImWebService imWebService;
    
    @PostMapping("/image")
    public CompletableFuture<ResponseEntity<ChatResponseDTO>> createPerfume(@ModelAttribute ChatRequestDTO chatData) {
        if (chatData.getImage() == null || chatData.getImage().isEmpty()) {
            throw new IllegalArgumentException("Image data is required.");
        }
        return chatService.createPerfume(chatData)
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping("/products")
    public ResponseEntity<String> getProduct(@RequestBody String entity) {
        return new ResponseEntity<>(imWebService.getProduct(), HttpStatus.OK);
    }
    
    
}
