package com.acscent.chatdemo2.dto;

import lombok.Data;

@Data
public class UserCodeRequestDTO {
    private String code;

    public UserCodeRequestDTO(String code) {
        this.code = code;
    }

    public UserCodeRequestDTO() {}
}
