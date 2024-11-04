package com.acscent.chatdemo2.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
public class PerfumeRequestDTO {

    private String name;
    private String gender;
    private String language;
    private String code;
    private String keyword;
    private List<String> preferred;
    private List<String> disliked;
    private MultipartFile image;

    public PerfumeRequestDTO() {}

    @Builder
    public PerfumeRequestDTO(String name, String gender, String language, String code, String keyword, List<String> preferred, List<String> disliked, MultipartFile image) {
        this.name = name;
        this.gender = gender;
        this.language = language;
        this.code = code;
        this.keyword = keyword;
        this.preferred = preferred;
        this.disliked = disliked;
        this.image = image;
    }
}