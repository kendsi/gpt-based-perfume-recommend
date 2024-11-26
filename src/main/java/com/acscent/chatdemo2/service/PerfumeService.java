package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;

public interface PerfumeService {
    public PerfumeResponseDTO createPerfume(PerfumeRequestDTO chatRequest);
    public PerfumeResponseDTO getResult(String uuid);
}
