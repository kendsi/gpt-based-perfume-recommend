package com.acscent.chatdemo2.data;

import java.util.List;

import lombok.Data;

@Data
public class Preference {
    private List<Scent> preferred;
    private List<Scent> disliked;

    @Data
    public class Scent {
        private int id;
        private String label;
    }
}