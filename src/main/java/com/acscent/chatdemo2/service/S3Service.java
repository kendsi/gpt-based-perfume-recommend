package com.acscent.chatdemo2.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    public String uploadImage(MultipartFile image, String userName);
}
