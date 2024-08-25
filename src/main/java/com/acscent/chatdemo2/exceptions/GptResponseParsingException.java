package com.acscent.chatdemo2.exceptions;

public class GptResponseParsingException extends RuntimeException {
    public GptResponseParsingException(String message) {
        super(message);
    }
}
