package com.acscent.chatdemo2.dto;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private int status;
    private String error;
    private String message;
    private String timestamp;

    public ErrorResponseDTO(HttpStatus status, String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.timestamp = Instant.now().toString();
    }
}