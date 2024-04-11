package com.app.sns.aiproduct.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.service.GoogleVoiceSerice;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class GoogleVoiceSericeImpl implements GoogleVoiceSerice {
    @Value("${googleCloud.apiKey}")
    private String googleCloud_apiKey;

    private final String googleCloud_apiUrl = "https://texttospeech.googleapis.com/v1/text:synthesize?key=";
    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(200))
            .setReadTimeout(Duration.ofSeconds(200))
            .build();


    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000)) // 最多重试3次，每次间隔1秒
    private String sendGoogleVoicePost(String payload) throws ServiceException {

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        // 发送请求
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(googleCloud_apiUrl+ googleCloud_apiKey, requestEntity, String.class);

        return responseEntity.getBody();
    }


    public String googleCloudTextToSpeech(String content){

        try {
            JSONObject audioConfig = new JSONObject();
            audioConfig.put("audioEncoding", "LINEAR16");
            audioConfig.put("effectsProfileId", new String[]{"small-bluetooth-speaker-class-device"});
            audioConfig.put("pitch", 0);
            audioConfig.put("speakingRate", 1);

            JSONObject input = new JSONObject();
            input.put("text", content);
            JSONObject voice = new JSONObject();

            String languageCode = "ja-JP";
            voice.put("languageCode", languageCode);
            voice.put("name", "ja-JP-Standard-A");
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("audioConfig", audioConfig);
            jsonPayload.put("input", input);
            jsonPayload.put("voice", voice);
            String jsonResponse = sendGoogleVoicePost(jsonPayload.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(jsonResponse);
            log.debug(jsonResponse);
            return jsonObject.getString("audioContent");
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("调用google text to speech错误：{}", e.getMessage());
            throw new ServiceException(401, e.getMessage());
        }
    }
}
