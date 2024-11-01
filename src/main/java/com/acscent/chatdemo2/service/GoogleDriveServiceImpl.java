package com.acscent.chatdemo2.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.exceptions.GoogleApiException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private static final String APPLICATION_NAME = "perfume-maker";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String GOOGLE_ACCOUNT_PATH = "/perfume-maker-google.json";

    private static final List<String> DRIVE_SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    
    private Drive driveService;

    @PostConstruct
    private void initDrive() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getDriveCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
        } catch (Exception e) {
            throw new GoogleApiException("Failed to initialize Google Drive API", e);
        }
    }

    private GoogleCredentials getDriveCredentials() throws IOException {
        // GoogleCredentials를 사용하여 서비스 계정 자격 증명 로드
        InputStream serviceAccountStream = GoogleDriveService.class.getResourceAsStream(GOOGLE_ACCOUNT_PATH);

        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                .createScoped(DRIVE_SCOPES);

        return credentials;
    }

    @Override
    @Async
    public CompletableFuture<Void> uploadImage(PerfumeRequestDTO chatRequest) {
        Path tempFile = null;
        try {
            MultipartFile image = chatRequest.getImage();
            String userName = chatRequest.getName();
            String fileExtension = image.getOriginalFilename();
            if (fileExtension != null && fileExtension.contains(".")) {
                // 마지막 점(.) 이후의 문자열을 추출하여 확장자로 사용
                fileExtension = fileExtension.substring(fileExtension.lastIndexOf("."));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String formattedDateTime = LocalDateTime.now().format(formatter);

            // 임시 파일 생성
            tempFile = Files.createTempFile("upload-", ".tmp");
            Files.copy(new BufferedInputStream(image.getInputStream()), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Google Drive에 업로드
            File fileMetadata = new File();
            fileMetadata.setName(formattedDateTime + fileExtension + "-" + userName);
            fileMetadata.setParents(Collections.singletonList("1r03AYNA4QF3_60B8FjA1FuKV_34dCwI8"));

            FileContent mediaContent = new FileContent(image.getContentType(), tempFile.toFile());

            File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

            log.info("File ID: " + file.getId());

        } catch (IOException e) {
            throw new GoogleApiException("Failed to upload image to Google Drive", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile);
                } catch (IOException e) {
                    log.error("Failed to delete temporary file.", e);
                }
            }
        }

        return CompletableFuture.completedFuture(null);
    }
}
