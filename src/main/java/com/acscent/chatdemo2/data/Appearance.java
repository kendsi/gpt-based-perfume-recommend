package com.acscent.chatdemo2.data;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Appearance {
    private String facialFeature;
    private String style;
    private String vibe;

    public Appearance() {}

    public Appearance(String facialFeature, String style, String vibe) {
        this.facialFeature = facialFeature;
        this.style = style;
        this.vibe = vibe;
    }
}