package com.app.sns.aiproduct.controller;


import com.app.sns.aiproduct.response.ChatResponse;
import com.app.sns.aiproduct.service.ChatGPTService;
import com.app.sns.aiproduct.service.GoogleVoiceSerice;
import com.app.sns.aiproduct.vo.ChatVO;
import com.app.sns.aiproduct.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Resource
    private ChatGPTService chatGPTService;
    @Resource
    private GoogleVoiceSerice googleVoiceSerice;
    @Autowired
    public ChatController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping("/sendMessage")
    public JsonResult<String> generateResponse(@Validated @RequestBody ChatVO chatVO) {
        //        jsonResult.setData("请问有什么可以帮到您的吗？");

        return JsonResult.ok(chatGPTService.generateResponse(chatVO.getMessage()));
    }


    @PostMapping("/sendMessageByGoogleCloud")
    public JsonResult<ChatResponse> sendMessageByGoogleCloud(@Validated @RequestBody ChatVO chatVO) {
        //        jsonResult.setData("请问有什么可以帮到您的吗？");
        String content = chatGPTService.generateResponse(chatVO.getMessage());
        String audioContent = googleVoiceSerice.googleCloudTextToSpeech(content);

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setContent(content);
        chatResponse.setAudioContent(audioContent);
        return JsonResult.ok(chatResponse);
    }

    /**
     * ユーザのコンテントをopenaiに送信して返事を音声化する
     * @param chatVOList
     * @return
     */
    @PostMapping("/sendContentByGoogleCloud")
    public JsonResult<ChatResponse> sendContentByGoogleCloud(@Validated @RequestBody List<ChatVO> chatVOList) {
        String content = chatGPTService.generateResponse(chatVOList);
        String audioContent = googleVoiceSerice.googleCloudTextToSpeech(content);

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setContent(content);
        chatResponse.setAudioContent(audioContent);
        return JsonResult.ok(chatResponse);
    }
}