package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.mapper.GptApiMapper;
import com.app.sns.aiproduct.pojo.entity.GptKey;
import com.app.sns.aiproduct.service.ChatGptApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatGptApiServiceImpl implements ChatGptApiService {
    @Autowired
    private GptApiMapper gptApiMapper;

    @Override
    public String getChatgptApiKey() {
        GptKey gptKey = gptApiMapper.selectById(1);
        return gptKey.getChatgptApiKey();
    }

    @Override
    public String getGooleCloudApiKey() {
        GptKey gptKey = gptApiMapper.selectById(1);
        return gptKey.getGooleCloudApiKey();
    }
}
