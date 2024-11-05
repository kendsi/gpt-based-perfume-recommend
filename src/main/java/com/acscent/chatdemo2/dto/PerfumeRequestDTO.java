package com.acscent.chatdemo2.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
public class PerfumeRequestDTO {

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "사용자 성별", example = "남성")
    private String gender;

    @Schema(description = "언어 설정", example = "kor")
    private String language;

    @Schema(description = "코드", example = "001001")
    private String code;

    @Schema(description = "키워드", example = "차가운")
    private String keyword;

    @Schema(description = "선호 향", example = "[\"citrus\", \"floral\"]")
    private List<String> preferredScent;

    @Schema(description = "비선호 향", example = "[\"musk\", \"spicy\"]")
    private List<String> dislikedScent;

    @Schema(description = "업로드할 이미지 파일", type = "string", format = "binary", required = true)
    private MultipartFile image;

    public PerfumeRequestDTO() {}

    @Builder
    public PerfumeRequestDTO(String name, String gender, String language, String code, String keyword, List<String> preferredScent, List<String> dislikedScent, MultipartFile image) {
        this.name = name;
        this.gender = gender;
        this.language = language;
        this.code = code;
        this.keyword = keyword;
        this.preferredScent = preferredScent;
        this.dislikedScent = dislikedScent;
        this.image = image;
    }
}