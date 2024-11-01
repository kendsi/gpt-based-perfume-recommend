package com.acscent.chatdemo2.service;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;

public interface PerfumeService {
    PerfumeResponseDTO createPerfume(PerfumeRequestDTO chatRequest);
}
