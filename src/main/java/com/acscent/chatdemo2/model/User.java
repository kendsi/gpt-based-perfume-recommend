package com.acscent.chatdemo2.model;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    
    private String name;
    private String id;
    // 전화번호 등
    private String code;

    private Date birthDay;

    private Boolean flag;

    private GENDER gender;

    public enum GENDER {
        MALE, FEMALE
    }

    @Builder
    public User(String name, String id, Date birthDay, GENDER gender) {
        this.name = name;
        this.id = id;
        this.birthDay = birthDay;
        this.gender = gender;
    }
}