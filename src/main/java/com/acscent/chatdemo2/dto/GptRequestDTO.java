package com.acscent.chatdemo2.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class GptRequestDTO {
    private String model;
    private List<Message> messages;

    @Getter
    @Setter
    public static class Message {
        private String role;
        private Object content;

        public Message() {}

        @JsonIgnore
        public boolean isContentString() {
            return content instanceof String;
        }

        @JsonIgnore
        public boolean isContentList() {
            return content instanceof List;
        }

        @JsonIgnore
        public String getContentAsString() {
            return isContentString() ? (String) content : null;
        }

        @JsonIgnore
        public List<Content> getContentAsList() {
            return isContentList() ? (List<Content>) content : null;
        }
    }

    @Getter
    @Setter
    public static class Content {
        private String type;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String text;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("image_url")
        private ImageUrl imageUrl;

        public Content() {}

        @Getter
        @Setter
        public static class ImageUrl {
            private String url;
        }
    }
}
