package com.acscent.chatdemo2.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class ChatRequestDTO {

    private String name;
    private String gender;
    private String language;
    private String code;
    private MultipartFile image;
}