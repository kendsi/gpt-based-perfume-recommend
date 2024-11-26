package com.acscent.chatdemo2.exceptions;

public class PerfumeNotFoundException extends RuntimeException {
    public PerfumeNotFoundException(String message) {
        super(message);
    }
}
