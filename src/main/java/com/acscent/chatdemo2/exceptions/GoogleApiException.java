package com.acscent.chatdemo2.exceptions;

public class GoogleApiException extends RuntimeException {
    public GoogleApiException(String message, Throwable e) {
        super(message, e);
    }
}
