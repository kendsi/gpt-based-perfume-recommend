package com.acscent.chatdemo2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.dto.ChatResponseDTO;
import com.acscent.chatdemo2.dto.PassCodeRequestDTO;
import com.acscent.chatdemo2.dto.PassCodeResponseDTO;
import com.acscent.chatdemo2.service.ChatService;
import com.acscent.chatdemo2.service.GoogleSheetsService;
import com.acscent.chatdemo2.service.ImWebService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.Collections;

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
    private final GoogleSheetsService googleSheetsService;
    private final ImWebService imWebService;

    @PostMapping("/passcode")
    public CompletableFuture<ResponseEntity<PassCodeResponseDTO>> verifyPassCode(@RequestBody PassCodeRequestDTO passCodeRequestDTO) {
        if (passCodeRequestDTO.getCode().equals("") || passCodeRequestDTO.getCode() == null) {
            throw new IllegalArgumentException("Please Enter Code");
        }
        return googleSheetsService.verifyCode(passCodeRequestDTO.getCode())
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }
    
    
    @PostMapping("/image")
    public CompletableFuture<ResponseEntity<ChatResponseDTO>> createPerfume(@ModelAttribute ChatRequestDTO chatData) {
        if (chatData.getImage() == null || chatData.getImage().isEmpty()) {
            throw new IllegalArgumentException("Image data is required.");
        }
        return chatService.createPerfume(chatData)
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping("/products")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getProducts() {
        return imWebService.getAccessToken()
            .thenCompose(imWebService::getOrders)
            .thenApply(orders -> new ResponseEntity<>(orders, HttpStatus.OK))
            .exceptionally(ex -> {
                // 오류 발생 시 에러 응답 반환
                return new ResponseEntity<>(Collections.singletonMap("error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            });
    }
}
