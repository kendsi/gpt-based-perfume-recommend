package com.acscent.chatdemo2.exceptions;

public class GptImageProcessingException extends RuntimeException {
    public GptImageProcessingException(String message) {
        super(message);
    }   
}