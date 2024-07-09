package com.acscent.chatdemo2.service;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.model.User;
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
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import jakarta.annotation.PostConstruct;

@Service
public class GoogleService {

    private static final String APPLICATION_NAME = "winter-jet-186516";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> DRIVE_SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final List<String> SHEET_SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String GOOGLE_ACCOUNT_PATH = "/credentials.json";
    
    private Drive driveService;
    private Sheets sheetService;

    @PostConstruct
    private void initDrive() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getDriveCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
    @PostConstruct
    private void initSheets() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        sheetService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getSheetCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    private static Credential getDriveCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleService.class.getResourceAsStream(GOOGLE_ACCOUNT_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + GOOGLE_ACCOUNT_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, DRIVE_SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static Credential getSheetCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleService.class.getResourceAsStream(GOOGLE_ACCOUNT_PATH);
        if (in == null) {
        throw new FileNotFoundException("Resource not found: " + GOOGLE_ACCOUNT_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SHEET_SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void uploadImage(MultipartFile image) throws IOException {
        
        // 임시 파일 생성
        Path tempFile = Files.createTempFile("upload-", ".tmp");
        Files.copy(new BufferedInputStream(image.getInputStream()), tempFile, StandardCopyOption.REPLACE_EXISTING);

        // Google Drive에 업로드
        File fileMetadata = new File();
        fileMetadata.setName(image.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList("1FHpi3xOpPdW5eEQVIH1qFDM4HO5By9mY"));

        FileContent mediaContent = new FileContent(image.getContentType(), tempFile.toFile());

        File file = driveService.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute();

        System.out.println("File ID: " + file.getId());

        // 임시 파일 삭제
        Files.delete(tempFile);
    }

    public void findByCode(String userCode) throws IOException {
        String spreadsheetId = "1I_g1hf-1Ghnp2C2HqV9G5ECszYNfpVPnqOr56HAmMyw";
        String range = "sheet1!A2:C";

        ValueRange response = sheetService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                if (row.size() > 1 && userCode.equals(row.get(1))) { // 열 B에서 값 검색
                    if (row.size() > 2) { // 열 C에 데이터가 있는지 확인
                        System.out.println("Found value in column B: " + row.get(1));
                        System.out.println("Corresponding value in column C: " + row.get(2));
                    } else {
                        System.out.println("Found value in column B: " + row.get(1));
                        System.out.println("No corresponding value in column C");
                    }
                }
            }
        }
    }
}