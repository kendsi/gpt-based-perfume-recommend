package com.acscent.chatdemo2.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.data.Appearance;
import com.acscent.chatdemo2.data.ParsedGptResponse;
import com.acscent.chatdemo2.exceptions.GptImageProcessingException;
import com.acscent.chatdemo2.exceptions.GptResponseParsingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GptResponseParser {
    private static final Pattern PERFUME_NAME_PATTERN = Pattern.compile("RECOMMENDED PERFUME: (.*?)\\n", Pattern.DOTALL);

    private static final Pattern FACIAL_FEATURE_PATTERN = Pattern.compile("FACIAL FEATURE: (.*?)\\n", Pattern.DOTALL);
    private static final Pattern STYLE_PATTERN = Pattern.compile("STYLE: (.*?)\\n", Pattern.DOTALL);
    private static final Pattern VIBE_PATTERN = Pattern.compile("VIBE: (.*?)\\n", Pattern.DOTALL);

    private static final Pattern TOP_NOTE_NAME_PATTERN = Pattern.compile("TOP NOTE: (.*?)\\n", Pattern.DOTALL);
    private static final Pattern MIDDLE_NOTE_NAME_PATTERN = Pattern.compile("MIDDLE NOTE: (.*?)\\n", Pattern.DOTALL);
    private static final Pattern BASE_NOTE_NAME_PATTERN = Pattern.compile("BASE NOTE: (.*?)\\n", Pattern.DOTALL);

    private static final Pattern PROFILE_PATTERN = Pattern.compile("PROFILE(.*)$", Pattern.DOTALL);

    private static final Pattern ERROR_PATTERN = Pattern.compile("ERROR: (.*)$", Pattern.DOTALL);

    public ParsedGptResponse parseGptResponse(String content) {

        String errorMessage = extractPattern(ERROR_PATTERN, content, false);
        if (!errorMessage.isEmpty()) {
            throw new GptImageProcessingException("An error occurred while GPT was analyzing the image: " + errorMessage);
        }

        String perfumeName = extractPattern(PERFUME_NAME_PATTERN, content, true).trim();

        String facialFeature = extractPattern(FACIAL_FEATURE_PATTERN, content, true).trim();
        String style = extractPattern(STYLE_PATTERN, content, true).trim();
        String vibe = extractPattern(VIBE_PATTERN, content, true).trim();
        Appearance appearance = new Appearance(facialFeature, style, vibe);

        String topNoteName = extractPattern(TOP_NOTE_NAME_PATTERN, content, true).trim();
        String middleNoteName = extractPattern(MIDDLE_NOTE_NAME_PATTERN, content, true).trim();
        String baseNoteName = extractPattern(BASE_NOTE_NAME_PATTERN, content, true).trim();

        String profile = extractPattern(PROFILE_PATTERN, content, true).trim();

        return ParsedGptResponse.builder()
                                .perfumeName(perfumeName)
                                .topNote(topNoteName)
                                .middleNote(middleNoteName)
                                .baseNote(baseNoteName)
                                .appearance(appearance)
                                .profile(profile)
                                .build();
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