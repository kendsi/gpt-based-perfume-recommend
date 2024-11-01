package com.acscent.chatdemo2.dto;

import lombok.Data;

@Data
public class UserCodeResponseDTO {
    private String status;
    private String code;

    public UserCodeResponseDTO(String status, String code) {
        this.status = status;
        this.code = code;
    }

    public UserCodeResponseDTO() {}
}