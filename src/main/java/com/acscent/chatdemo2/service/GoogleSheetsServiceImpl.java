package com.acscent.chatdemo2.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.exceptions.GoogleApiException;
import com.acscent.chatdemo2.exceptions.SheetsValueException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final String APPLICATION_NAME = "winter-jet-186516";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SHEET_SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String GOOGLE_ACCOUNT_PATH = "/credentials.json";
    
    private Sheets sheetService;

    @PostConstruct
    private void initSheets() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            sheetService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getSheetCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        } catch (Exception e) {
            throw new GoogleApiException("Failed to initialize Google Drive API", e);
        }
    }

    private Credential getSheetCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleSheetsServiceImpl.class.getResourceAsStream(GOOGLE_ACCOUNT_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + GOOGLE_ACCOUNT_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SHEET_SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }

    @Override
    @Async
    public CompletableFuture<String> findByCode(String code) {

        String spreadsheetId = "1I_g1hf-1Ghnp2C2HqV9G5ECszYNfpVPnqOr56HAmMyw";
        String range = "sheet1!A1:C";

        try {
            ValueRange response = sheetService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
        
            if (values == null || values.isEmpty()) {
                throw new SheetsValueException("No data found in sheets: " + spreadsheetId);
            }

            int serialNumberColumnIndex = -1;
            List<Object> headerRow = values.get(0);  // 첫 번째 행

            for (int i = 0; i < headerRow.size(); i++) {
                if (headerRow.get(i).equals("일련번호")) {
                    serialNumberColumnIndex = i;
                    break;
                }
            }

            if (serialNumberColumnIndex == -1) {
                throw new SheetsValueException("'일련번호' 열을 찾을 수 없습니다.");
            }
        
            for (List<Object> row : values.subList(1, values.size())) {  // 첫 번째 행 이후부터 탐색
                if (row.size() > serialNumberColumnIndex && code.equals(row.get(serialNumberColumnIndex))) {
                    if (row.size() > serialNumberColumnIndex + 1) {
                        log.info("Found value in '일련번호' column: " + row.get(serialNumberColumnIndex));
                        log.info("Corresponding value in next column: " + row.get(serialNumberColumnIndex + 1));
                        return CompletableFuture.completedFuture(row.get(serialNumberColumnIndex + 1).toString());
                    } else {
                        log.info("Found value in '일련번호' column: " + row.get(serialNumberColumnIndex));
                        throw new SheetsValueException("No corresponding value in the next column.");
                    }
                }
            }
    
            throw new SheetsValueException("Code not found: " + code);
        } catch (IOException e) {
            throw new GoogleApiException("Could not access to the sheets: " + spreadsheetId, e);
        }
    }
}
