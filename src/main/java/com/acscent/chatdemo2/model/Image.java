package com.acscent.chatdemo2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {

    private String decodedUrl;
    private String encodedUrl;

    public Image(String decodedUrl, String encodedUrl) {
        this.decodedUrl = decodedUrl;
        this.encoding(decodedUrl);
    }

    public void encoding(String decodedUrl) {
        // Some encoding logic
        this.encodedUrl = decodedUrl;
    }

    public void decoding(String encodedUrl) {
        // Some decoding logic
        this.decodedUrl = encodedUrl;
    }
}
