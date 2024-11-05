package com.acscent.chatdemo2.service;

import org.springframework.stereotype.Service;

import com.acscent.chatdemo2.dto.UserCodeResponseDTO;
import com.acscent.chatdemo2.exceptions.CodeAlreadyUsedException;
import com.acscent.chatdemo2.exceptions.CodeNotFoundException;
import com.acscent.chatdemo2.model.UserCode;
import com.acscent.chatdemo2.repository.UserCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCodeServiceImpl implements UserCodeService {

    private final UserCodeRepository userCodeRepository;

    @Override
    public UserCodeResponseDTO verifyCode(String code) {
        UserCode userCode = userCodeRepository.findByCode(code)
                                            .orElseThrow(() -> new CodeNotFoundException(code));

        if (!userCode.isEnabled()) {
            throw new CodeAlreadyUsedException(code);
        }

        userCode.setEnabled(false);
        userCodeRepository.save(userCode);

        return new UserCodeResponseDTO("validated", code);
    }
}
