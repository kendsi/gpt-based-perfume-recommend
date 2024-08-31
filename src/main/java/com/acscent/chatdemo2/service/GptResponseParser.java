package com.acscent.chatdemo2.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.dto.ChatResponseDTO;
import com.acscent.chatdemo2.exceptions.GptResponseParsingException;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import java.util.List;

@Slf4j
@Service
public class GptResponseParser {

    private static final Pattern INSIGHTS_PATTERN = Pattern.compile("Insight\\s\\d+:(.*?)(?=Insight\\s\\d+:|TOP NOTE:|$)", Pattern.DOTALL);
    private static final Pattern TOP_NOTE_PATTERN = Pattern.compile("TOP NOTE: (.*?)(?=\\n\\n|MIDDLE NOTE)", Pattern.DOTALL);
    private static final Pattern MIDDLE_NOTE_PATTERN = Pattern.compile("MIDDLE NOTE: (.*?)(?=\\n\\n|BASE NOTE)", Pattern.DOTALL);
    private static final Pattern BASE_NOTE_PATTERN = Pattern.compile("BASE NOTE: (.*?)(?=\\n\\n|Perfume|$)", Pattern.DOTALL);
    private static final Pattern NAME_RECOMMENDATION_PATTERN = Pattern.compile("Recommendation: (.*?)(?=\\n\\n|checkcheck|$)", Pattern.DOTALL);

    public ChatResponseDTO parseGptResponse(String content) {
        String insights = extractAllPatterns(INSIGHTS_PATTERN, content)
                             .stream()
                             .collect(Collectors.joining(""));
        String topNote = extractPattern(TOP_NOTE_PATTERN, content, true).trim();
        String middleNote = extractPattern(MIDDLE_NOTE_PATTERN, content, true).trim();
        String baseNote = extractPattern(BASE_NOTE_PATTERN, content, true).trim();
        String nameRecommendation = extractPattern(NAME_RECOMMENDATION_PATTERN, content, true).trim();

        return ChatResponseDTO.builder()
                .perfumeName(nameRecommendation)
                .insights(insights)
                .topNote(topNote)
                .middleNote(middleNote)
                .baseNote(baseNote)
                .build();
    }

    private List<String> extractAllPatterns(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.results()
                      .map(result -> result.group(1).trim())
                      .collect(Collectors.toList());
    }

    private String extractPattern(Pattern pattern, String content, boolean throwIfNotFound) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else if (throwIfNotFound) {
            throw new GptResponseParsingException("Required pattern not found in response: " + pattern.pattern());
        }
        return "";
    }
}