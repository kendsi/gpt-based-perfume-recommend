package com.acscent.chatdemo2.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
    
    private String name;
    private String code;
    private String gender;
}