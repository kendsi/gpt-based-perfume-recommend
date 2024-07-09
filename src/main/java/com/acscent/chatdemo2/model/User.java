package com.acscent.chatdemo2.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    
    private String name;
    private String email;
    // 전화번호 등
    private String code;

    private LocalDate birthDay;

    private Boolean flag;

    private GENDER gender;

    public enum GENDER {
        MALE, FEMALE
    }

    public User(String name, String email, String code, LocalDate birthDay, GENDER gender) {
        this.name = name;
        this.email = email;
        this.code = code;
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public User(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public User() {}
}