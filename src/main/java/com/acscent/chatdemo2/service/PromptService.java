package com.acscent.chatdemo2.service;

import java.util.List;

import com.acscent.chatdemo2.dto.GptRequestDTO.Message;
import com.acscent.chatdemo2.dto.PerfumeRequestDTO;

public interface PromptService {
    public List<Message> formatPrompt(PerfumeRequestDTO perfumeRequest, List<Message> prompt, String notesPrompt);
    public List<Message> loadPrompt(String language);
}
