package com.acscent.chatdemo2.controller;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;
import com.acscent.chatdemo2.dto.UserCodeRequestDTO;
import com.acscent.chatdemo2.dto.UserCodeResponseDTO;
import com.acscent.chatdemo2.service.PerfumeService;
import com.acscent.chatdemo2.service.UserCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.acscent.chatdemo2.service.ImWebService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final PerfumeService perfumeService;
    private final UserCodeService userCodeService;
    private final ImWebService imWebService;

    @PostMapping("/passcode")
    public ResponseEntity<UserCodeResponseDTO> verifyPassCode(@RequestBody UserCodeRequestDTO userCodeRequestDTO) {
        String code = userCodeRequestDTO.getCode();
        if (code.equals("") || code == null) {
            throw new IllegalArgumentException("Please Enter Code");
        }
        return new ResponseEntity<>(userCodeService.verifyCode(code), HttpStatus.OK);
    }
    
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerfumeResponseDTO> createPerfume(@ModelAttribute PerfumeRequestDTO perfumeDto) {
        if (perfumeDto.getImage() == null || perfumeDto.getImage().isEmpty()) {
            throw new IllegalArgumentException("Image data is required.");
        }
        PerfumeResponseDTO perfume = perfumeService.createPerfume(perfumeDto);

        return new ResponseEntity<>(perfume, HttpStatus.OK);
    }

    @Operation(summary = "이미지 파일 가져오기", description = "주어진 이미지 이름에 해당하는 이미지를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 파일이 성공적으로 반환됨",
                content = @Content(mediaType = "image/jpeg", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    @GetMapping("/image/{imageName}")
    public ResponseEntity<Resource> getImage(
        @Parameter(description = "가져올 이미지 파일의 이름", example = "2024-11-06-02-09-45-홍길동.jpg")
        @PathVariable("imageName") String imageName) {
        try {
            Resource resource = perfumeService.getImage(imageName);
            String contentType = resource.getFile().toURI().toURL().openConnection().getContentType();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    

    @GetMapping("/products")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getProducts() {
        return imWebService.getAccessToken()
            .thenCompose(imWebService::getOrders)
            .thenApply(orders -> new ResponseEntity<>(orders, HttpStatus.OK))
            .exceptionally(ex -> {
                // 오류 발생 시 에러 응답 반환
                return new ResponseEntity<>(Collections.singletonMap("error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            });
    }
}
