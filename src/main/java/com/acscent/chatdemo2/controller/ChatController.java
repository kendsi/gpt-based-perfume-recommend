package com.acscent.chatdemo2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;
import com.acscent.chatdemo2.dto.UserCodeRequestDTO;
import com.acscent.chatdemo2.dto.UserCodeResponseDTO;
import com.acscent.chatdemo2.service.PerfumeService;
import com.acscent.chatdemo2.service.UserCodeService;
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

    private final PerfumeService perfumeService;
    private final UserCodeService userCodeService;
    private final ImWebService imWebService;

    @PostMapping("/passcode")
    public ResponseEntity<UserCodeResponseDTO> verifyPassCode(@RequestBody UserCodeRequestDTO userCodeRequestDTO) {
        String code = userCodeRequestDTO.getCode();
        if (code.equals("") || code == null) {
            throw new IllegalArgumentException("Please Enter Code");
        }
        return new ResponseEntity<>(userCodeService.verifyCode(code), HttpStatus.OK);
    }
    
    
    @PostMapping("/image")
    public ResponseEntity<PerfumeResponseDTO> createPerfume(@ModelAttribute PerfumeRequestDTO perfumeDto) {
        if (perfumeDto.getImage() == null || perfumeDto.getImage().isEmpty()) {
            throw new IllegalArgumentException("Image data is required.");
        }
        PerfumeResponseDTO perfume = perfumeService.createPerfume(perfumeDto);

        return new ResponseEntity<>(perfume, HttpStatus.OK);
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
