package com.acscent.chatdemo2.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.dto.ChatRequestDTO;
import com.acscent.chatdemo2.dto.ChatResponseDTO;
import com.acscent.chatdemo2.exceptions.CodeAlreadyUsedException;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final String APPLICATION_NAME = "perfume-maker";
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
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }

    @Override
    @Async
    public CompletableFuture<String> verifyCode(String code) {
        String spreadsheetId = "1I_g1hf-1Ghnp2C2HqV9G5ECszYNfpVPnqOr56HAmMyw";
        String range = "sheet1!A1:C";
    
        try {
            // Google Sheets에서 데이터를 가져오는 동기 작업
            ValueRange response = sheetService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
    
            if (values == null || values.isEmpty()) {
                throw new SheetsValueException("No data found in sheets: " + spreadsheetId);
            }
    
            // 'C'열의 인덱스는 2로 고정 ('A'는 0, 'B'는 1, 'C'는 2)
            int codeColumnIndex = 1; // 'B'열 인덱스 (코드가 있는 열)
            int flagColumnIndex = 2; // 'C'열 인덱스 (TRUE/FALSE 값이 있는 열)
    
            // 데이터를 찾아 코드가 일치하는 행을 검색합니다.
            for (int rowIndex = 1; rowIndex < values.size(); rowIndex++) {  // 첫 번째 행 이후부터 탐색
                List<Object> row = values.get(rowIndex);
                if (row.size() > codeColumnIndex && code.equals(row.get(codeColumnIndex))) {
                    if (row.size() > flagColumnIndex) {
                        String currentValue = row.get(flagColumnIndex).toString();
                        log.info("Found value in '일련번호' column: " + row.get(codeColumnIndex));
                        log.info("Corresponding value in 'C' column: " + currentValue);
    
                        // 값이 TRUE면 FALSE로 업데이트, FALSE면 예외 던지기
                        if ("TRUE".equalsIgnoreCase(currentValue)) {
                            // 'C'열의 셀 값을 업데이트하는 메서드 호출
                            String cellRange = String.format("sheet1!C%d", rowIndex + 1); // 'C'열의 특정 행
                            updateCellValue(spreadsheetId, cellRange, "FALSE"); // updateCellValue 메서드를 사용하여 값 업데이트
                            return CompletableFuture.completedFuture("TRUE");
                        } else {
                            throw new CodeAlreadyUsedException(code);
                        }
                    } else {
                        log.info("Found value in '일련번호' column: " + row.get(codeColumnIndex));
                        throw new SheetsValueException("No corresponding value in the 'C' column.");
                    }
                }
            }
    
            throw new SheetsValueException("Code not found: " + code);
        } catch (IOException e) {
            throw new GoogleApiException("Could not access to the sheets: " + spreadsheetId, e);
        }
    }

    private void updateCellValue(String spreadsheetId, String cellRange, Object newValue) throws IOException {
        List<List<Object>> values = List.of(List.of(newValue)); // 셀에 저장할 데이터 구조 만들기
        ValueRange body = new ValueRange().setValues(values);

        sheetService.spreadsheets().values()
                .update(spreadsheetId, cellRange, body)
                .setValueInputOption("RAW")
                .execute();

        log.info("Updated cell at {} to '{}'", cellRange, newValue);
    }

    @Override
    @Async
    public CompletableFuture<String> getNotesPrompt() {
        String spreadsheetId = "185ekvjK6jSP_uFw9y_v6fEzNRZf8dSIM8YwQu7AplOo";
        String range = "sheet1!A2:E31";

        try {
            // Google Sheets에서 데이터를 가져오는 동기 작업
            ValueRange response = sheetService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            if (values == null || values.isEmpty()) {
                throw new SheetsValueException("No data found in sheets: " + spreadsheetId);
            }

            // 노트별로 데이터를 분리하고 최소 count 값보다 3 큰 데이터만 필터링
            List<String> topNotesPrompt = filterDataByNoteType(values, "TOPNOTE");
            List<String> middleNotesPrompt = filterDataByNoteType(values, "MIDDLENOTE");
            List<String> baseNotesPrompt = filterDataByNoteType(values, "BASENOTE");

            // 최종 결과 문자열 생성
            StringBuilder resultBuilder = new StringBuilder();

            resultBuilder.append("Top Note 향 오일 리스트:\n");
            topNotesPrompt.forEach(resultBuilder::append);

            resultBuilder.append("\nMiddle Note 향 오일 리스트:\n");
            middleNotesPrompt.forEach(resultBuilder::append);

            resultBuilder.append("\nBase Note 향 오일 리스트:\n");
            baseNotesPrompt.forEach(resultBuilder::append);

            return CompletableFuture.completedFuture(resultBuilder.toString());

        } catch (IOException e) {
            throw new GoogleApiException("Could not access the sheets: " + spreadsheetId, e);
        }
    }

    private List<String> filterDataByNoteType(List<List<Object>> values, String noteType) {
        // 해당 노트 유형의 데이터만 필터링
        List<List<Object>> noteData = values.stream()
                .filter(row -> row.size() > 0 && noteType.equals(row.get(0).toString()))
                .collect(Collectors.toList());
    
        // 최소 count 값을 찾음
        Optional<Integer> minCount = noteData.stream()
                .map(row -> Integer.parseInt(row.get(2).toString())) // count 값은 세 번째 열에 있음
                .min(Integer::compareTo);
    
        List<String> formattedPrompts = new ArrayList<>();
    
        if (minCount.isPresent()) {
            int threshold = minCount.get() + 3;
    
            // 최소 count 값보다 3 작은 데이터만 필터링하고, 포맷팅하여 결과에 추가
            noteData.stream()
                    .filter(row -> Integer.parseInt(row.get(2).toString()) < threshold)  // 여기에서 필터링 조건을 수정
                    .forEach(row -> formattedPrompts.add(formatNotePrompts(row)));
        }
    
        return formattedPrompts;
    }

    private String formatNotePrompts(List<Object> row) {
        String name = row.get(1).toString(); // NAME
        String description = row.get(3).toString(); // DESCRIPTION
        String recommendation = row.get(4).toString(); // RECOMMENDATION
    
        return String.format("%s\n향 묘사: %s\n추천 문구: %s\n\n", name, description, recommendation);
    }

    @Override
    @Async
    public CompletableFuture<Void> updateCount(String selectedNote) {
        String spreadsheetId = "185ekvjK6jSP_uFw9y_v6fEzNRZf8dSIM8YwQu7AplOo";
        String range = "sheet1!A2:E31";

        try {
            // Step 1: Fetch data from Google Sheets
            ValueRange response = sheetService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            if (values == null || values.isEmpty()) {
                throw new SheetsValueException("No data found in sheets: " + spreadsheetId);
            }

            boolean isUpdated = false;
            for (int i = 0; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (selectedNote.equals(row.get(1).toString())) { // 노트 이름 검색
                    int currentCount = Integer.parseInt(row.get(2).toString()); // 해당 노트의 현재 COUNT 값 저장
                    String cellRange = String.format("sheet1!C%d", i + 2); // 해당 노트의 COUNT 값 위치(인덱스) 저장
                    updateCellValue(spreadsheetId, cellRange, currentCount + 1); // updateCellValue 메서드를 사용하여 값 업데이트
                    isUpdated = true;
                    break;
                }
            }

            if (!isUpdated) {
                throw new SheetsValueException("Note not found: " + selectedNote);
            }

        } catch (IOException e) {
            throw new GoogleApiException("Failed to access Google Sheets: " + spreadsheetId, e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> saveChatResponse(ChatResponseDTO chatResponse, ChatRequestDTO chatRequest) {
        String spreadsheetId = "1iMLvUNs_mDDOcBQQdhtF4WDSDEK6ZGHJJGDMAtc2C3Y";
        String range = "sheet1!A:E";

        String code = chatRequest.getCode();
        String userName = chatRequest.getName();
        String perfumeName = chatResponse.getPerfumeName();
        String insights = chatResponse.getInsights();

        // 추가할 데이터 행을 생성
        List<Object> newRow = List.of(code, userName, perfumeName, insights);

        // ValueRange 객체를 생성하여 데이터를 설정
        ValueRange body = new ValueRange().setValues(List.of(newRow));

        try {
            // Google Sheets에 데이터를 추가
            sheetService.spreadsheets().values()
                    .append(spreadsheetId, range, body)
                    .setValueInputOption("RAW") // 데이터를 그대로 추가
                    .execute();

            log.info("Data saved successfully to Google Sheets.");

        } catch (IOException e) {
            log.error("Failed to save data to Google Sheets.", e);
            throw new GoogleApiException("Failed to save data to Google Sheets", e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
