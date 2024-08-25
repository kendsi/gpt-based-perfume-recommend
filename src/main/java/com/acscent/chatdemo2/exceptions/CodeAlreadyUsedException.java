package com.acscent.chatdemo2.exceptions;

public class CodeAlreadyUsedException extends RuntimeException {
    public CodeAlreadyUsedException(String code) {
        super("The Code: " + code + "Already Used.");
    }
}
