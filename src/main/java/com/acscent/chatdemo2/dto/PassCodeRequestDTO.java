package com.acscent.chatdemo2.dto;

import lombok.Data;

@Data
public class PassCodeRequestDTO {
    private String code;

    public PassCodeRequestDTO(String code) {
        this.code = code;
    }

    public PassCodeRequestDTO() {}
}
