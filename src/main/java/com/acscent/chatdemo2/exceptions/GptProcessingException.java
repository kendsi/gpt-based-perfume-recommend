package com.acscent.chatdemo2.exceptions;

public class GptProcessingException extends RuntimeException {
    public GptProcessingException(String message) {
        super(message);
    }
}
