package com.acscent.chatdemo2.dto;

import lombok.Data;

@Data
public class PassCodeResponseDTO {
    private String status;
    private String code;

    public PassCodeResponseDTO(String status, String code) {
        this.status = status;
        this.code = code;
    }

    public PassCodeResponseDTO() {}
}