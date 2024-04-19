package com.app.sns.aiproduct.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.service.ChatGPTService;
import com.app.sns.aiproduct.vo.ChatVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

import com.app.sns.aiproduct.mapper.GetGPTSettingMapper;
import com.app.sns.aiproduct.pojo.vo.ChatGPTSettingVO;


@Service
@Slf4j
public class ChatGPTServiceImpl implements ChatGPTService {

    @Value("${chatgpt.apiKey}")
    private String chatGpt_apiKey;

    private final String chatgpt_apiUrl = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(300))
            .setReadTimeout(Duration.ofSeconds(300))
            .build();

    @Autowired
    private GetGPTSettingMapper getGPTSettingMapper;

    public String generateResponse(String userMessage) {
        ChatGPTSettingVO settingInfo = getGPTSettingMapper.getGptSettingInfo("sns_interviewer");
        String prompt = escapeLineCode(settingInfo.getPromptcontent());
        String model = escapeLineCode(settingInfo.getModel());
            try {
            // log.debug(userMessage);
            //  payload
                JSONObject payloadJson = new JSONObject();
                payloadJson.put("model",model);
                JSONArray messages = new JSONArray();
                JSONObject message = new JSONObject();
                JSONObject promptMessage = new JSONObject();
                promptMessage.put("role","system");
                promptMessage.put("content",prompt);
                messages.add(promptMessage);
                message.put("role","user");
                message.put("content",userMessage);
                messages.add(message);
                payloadJson.put("messages",messages);
                log.debug(payloadJson.toJSONString());
            String jsonResponse = sendChatGptPost(payloadJson.toJSONString());

            JSONObject jsonObject = JSONObject.parseObject(jsonResponse);
            // log.debug(jsonObject.toJSONString());
            log.debug("completion_tokens: {}",jsonObject.getJSONObject("usage").getInteger("completion_tokens"));
            log.debug("prompt_tokens: {}",jsonObject.getJSONObject("usage").getInteger("prompt_tokens"));
            log.debug("total_tokens: {}",jsonObject.getJSONObject("usage").getInteger("total_tokens"));

            String assistantMessage = jsonObject.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            log.debug(jsonResponse);

            return assistantMessage;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("chatgptapiエラーを呼び出しました：{}", e.getMessage());
            throw new ServiceException(401, e.getMessage());
        }
    }

    public String generateResponse(List<ChatVO> chatVOList) {
        ChatGPTSettingVO settingInfo = getGPTSettingMapper.getGptSettingInfo("sns_interviewer");
        String prompt = escapeLineCode(settingInfo.getPromptcontent());
        String model = escapeLineCode(settingInfo.getModel());
        try {
            // log.debug(userMessage);
            //  payload
            JSONObject payloadJson = new JSONObject();
            payloadJson.put("model",model);
            JSONArray messages = new JSONArray();
            JSONObject promptMessage = new JSONObject();
            promptMessage.put("role","system");
            promptMessage.put("content",prompt);
            messages.add(promptMessage);
            for (ChatVO chatVO : chatVOList) {
                JSONObject message = new JSONObject();
                message.put("role",chatVO.getRole());
                message.put("content",chatVO.getContent());
                messages.add(message);
            }
            payloadJson.put("messages",messages);
            log.debug(payloadJson.toJSONString());
            String jsonResponse = sendChatGptPost(payloadJson.toJSONString());

            JSONObject jsonObject = JSONObject.parseObject(jsonResponse);
            // log.debug(jsonObject.toJSONString());
            log.debug("completion_tokens: {}",jsonObject.getJSONObject("usage").getInteger("completion_tokens"));
            log.debug("prompt_tokens: {}",jsonObject.getJSONObject("usage").getInteger("prompt_tokens"));
            log.debug("total_tokens: {}",jsonObject.getJSONObject("usage").getInteger("total_tokens"));

            String assistantMessage = jsonObject.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            log.debug(jsonResponse);

            return assistantMessage;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("chatgptapiエラーを呼び出しました：{}", e.getMessage());
            throw new ServiceException(401, e.getMessage());
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000)) // 1秒間隔で最大3回再試行します
    private String sendChatGptPost(String payload) throws ServiceException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + chatGpt_apiKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(chatgpt_apiUrl, requestEntity, String.class);

        return responseEntity.getBody();
    }


    private String escapeLineCode(String text) {
        // Escape LF
        String escapedText = text.replace("\n", "\\n");

        // Escape CRFL
        escapedText = escapedText.replace("\r\n", "\\r\\n");

        // Escape CR
        escapedText = escapedText.replace("\r", "\\r");

        return escapedText;
    }


}
