package com.acscent.chatdemo2.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Appearance {
    @Column(length = 2000)
    private String facialFeature;
    @Column(length = 2000)
    private String style;
    @Column(length = 2000)
    private String vibe;

    public Appearance() {}

    public Appearance(String facialFeature, String style, String vibe) {
        this.facialFeature = facialFeature;
        this.style = style;
        this.vibe = vibe;
    }
}