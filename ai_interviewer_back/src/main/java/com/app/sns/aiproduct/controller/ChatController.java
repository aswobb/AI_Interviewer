package com.app.sns.aiproduct.controller;


import com.app.sns.aiproduct.response.ChatResponse;
import com.app.sns.aiproduct.service.ChatGPTService;
import com.app.sns.aiproduct.service.GoogleVoiceSerice;
import com.app.sns.aiproduct.vo.ChatVO;
import com.app.sns.aiproduct.web.JsonResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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

    @PostMapping("/sendMessageCreateCSV")
    public JsonResult<String> sendMessageCreateCSV(@Validated @RequestBody ChatVO chatVO) {
        String content = chatGPTService.generateResponse(chatVO.getMessage());
        String csvData = content.replaceAll("```(csv)?\\n|```", "").trim();

        // 在 CSV 数据前添加 UTF-8 BOM
        byte[] bom = "\uFEFF".getBytes(StandardCharsets.UTF_8);
        byte[] csvDataBytes = csvData.getBytes(StandardCharsets.UTF_8);

        // 将 BOM 和 CSV 数据合并
        byte[] dataWithBom = new byte[bom.length + csvDataBytes.length];
        System.arraycopy(bom, 0, dataWithBom, 0, bom.length);
        System.arraycopy(csvDataBytes, 0, dataWithBom, bom.length, csvDataBytes.length);

        // 将数据写入 CSV 文件
        String filePath = "output11.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(new String(dataWithBom, StandardCharsets.UTF_8));
            System.out.println("CSV ファイルが正常に保存されました: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResult.ok(content);
    }

    @PostMapping("/sendMessage")
    public JsonResult<String> generateResponse(@Validated @RequestBody ChatVO chatVO) {
        //        jsonResult.setData("请问有什么可以帮到您的吗？");

        return JsonResult.ok(chatGPTService.generateResponse(chatVO.getMessage()));
    }

    @PostMapping("/receiveFile")
    public JsonResult<String> receiveFile(@RequestParam("file") MultipartFile file) {
        //        jsonResult.setData("请问有什么可以帮到您的吗？");

        if (file.isEmpty()) {
            return null;
        }
        try {

            // 加载 PDF 文件
            PDDocument document = PDDocument.load(file.getInputStream());

            // 获取 PDF 文件的内容
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // 关闭文档
            document.close();

            return  JsonResult.ok(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
     *
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