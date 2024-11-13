package com.acscent.chatdemo2.util;

import com.acscent.chatdemo2.data.Preference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyEditorSupport;

public class PreferenceEditor extends PropertyEditorSupport {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setAsText(String text) {
        try {
            Preference preference = objectMapper.readValue(text, Preference.class);
            setValue(preference);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid preference format", e);
        }
    }
}
