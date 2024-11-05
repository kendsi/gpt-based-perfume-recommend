package com.acscent.chatdemo2.service;

import org.springframework.core.io.Resource;

import com.acscent.chatdemo2.dto.PerfumeRequestDTO;
import com.acscent.chatdemo2.dto.PerfumeResponseDTO;

public interface PerfumeService {
    public PerfumeResponseDTO createPerfume(PerfumeRequestDTO chatRequest);
    public Resource getImage(String imageName);
}
