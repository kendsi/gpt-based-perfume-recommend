package com.acscent.chatdemo2.exceptions;

public class CodeNotFoundException extends RuntimeException {
    public CodeNotFoundException(String code) {
        super("The Code: " + code + " Not Founded.");
    }
}