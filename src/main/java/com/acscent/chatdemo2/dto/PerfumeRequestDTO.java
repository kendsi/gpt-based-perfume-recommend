package com.acscent.chatdemo2.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
public class PerfumeRequestDTO {

    private String name;
    private String gender;
    private String language;
    private String code;
    private MultipartFile image;

    public PerfumeRequestDTO() {}

    @Builder
    public PerfumeRequestDTO(String name, String gender, String language, String code, MultipartFile image) {
        this.name = name;
        this.gender = gender;
        this.language = language;
        this.code = code;
        this.image = image;
    }
}