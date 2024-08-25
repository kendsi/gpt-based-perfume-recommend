package com.acscent.chatdemo2.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.CompletableFuture;

public interface GoogleDriveService {
    CompletableFuture<Void> uploadImage(MultipartFile image);
}