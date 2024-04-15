package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.vo.ChatVO;

import java.util.List;

public interface ChatGPTService {
    public String generateResponse(String userMessage);
    public String generateResponse(List<ChatVO> chatVOList);

}
