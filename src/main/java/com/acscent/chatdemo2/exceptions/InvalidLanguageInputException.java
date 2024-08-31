package com.acscent.chatdemo2.exceptions;

public class InvalidLanguageInputException extends RuntimeException {
    public InvalidLanguageInputException(String message) {
        super(message);
    }
}
