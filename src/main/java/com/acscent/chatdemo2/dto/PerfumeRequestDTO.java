package com.acscent.chatdemo2.dto;

import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.data.Preference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
public class PerfumeRequestDTO {

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "사용자 성별", example = "남성")
    private String gender;

    @Schema(description = "언어 설정", example = "ko")
    private String language;

    @Schema(description = "키워드", example = "차가운")
    private String keyword;

    @Schema(description = "향 선호도")
    @JsonProperty("preference")
    private Preference preference;

    @Schema(description = "업로드할 이미지 파일", type = "string", format = "binary", required = true)
    private MultipartFile image;

    public PerfumeRequestDTO() {}

    @Builder
    public PerfumeRequestDTO(String name, String gender, String language, String keyword, Preference preference, MultipartFile image) {
        this.name = name;
        this.gender = gender;
        this.language = language;
        this.keyword = keyword;
        this.preference = preference;
        this.image = image;
    }
}