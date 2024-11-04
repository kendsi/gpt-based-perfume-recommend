package com.acscent.chatdemo2.service;

import java.util.List;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;

public interface PerfumeService {
    public PerfumeResponseDTO createPerfume(PerfumeRequestDTO chatRequest);
    public List<PerfumeResponseDTO> getAllPerfumeResults();
}
