package com.acscent.chatdemo2.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.exceptions.GoogleApiException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private static final String APPLICATION_NAME = "winter-jet-186516";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> DRIVE_SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String GOOGLE_ACCOUNT_PATH = "/credentials.json";
    
    private Drive driveService;

    @PostConstruct
    private void initDrive() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getDriveCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        } catch (Exception e) {
            throw new GoogleApiException("Failed to initialize Google Drive API", e);
        }
    }

    private Credential getDriveCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleDriveService.class.getResourceAsStream(GOOGLE_ACCOUNT_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + GOOGLE_ACCOUNT_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, DRIVE_SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }

    @Async
    public CompletableFuture<Void> uploadImage(MultipartFile image) {
        Path tempFile = null;
        try {
            // 임시 파일 생성
            tempFile = Files.createTempFile("upload-", ".tmp");
            Files.copy(new BufferedInputStream(image.getInputStream()), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Google Drive에 업로드
            File fileMetadata = new File();
            fileMetadata.setName(image.getOriginalFilename());
            fileMetadata.setParents(Collections.singletonList("1FHpi3xOpPdW5eEQVIH1qFDM4HO5By9mY"));

            FileContent mediaContent = new FileContent(image.getContentType(), tempFile.toFile());

            File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

            log.info("File ID: " + file.getId());

            return CompletableFuture.completedFuture(null);
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
    }
}
