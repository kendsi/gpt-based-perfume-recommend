package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.UserCodeResponseDTO;

public interface UserCodeService {
    public UserCodeResponseDTO verifyCode(String code);
}
