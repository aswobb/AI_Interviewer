package com.app.sns.aiproduct;

import com.alibaba.fastjson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class SpeechToTextHTTPExample {
    public static void main(String[] args) throws IOException {
        // 语音文件路径
        String audioFilePath = "output.wav";

        // 读取语音文件并转换为 Base64 字符串
        String audioContent = audioToBase64(audioFilePath);

        // 发送 HTTP POST 请求到 Google Cloud Speech-to-Text API
        String response = sendPostRequest(audioContent);

        // 解析识别结果并打印
        String transcript = parseTranscript(response);
        System.out.println("识别结果: " + transcript);
    }

    private static String audioToBase64(String filePath) throws IOException {
        byte[] audioBytes = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(audioBytes);
    }

    private static String sendPostRequest(String audioContent) throws IOException {
        String apiKey = "11"; // 替换为您的 Google Cloud API 密钥
        String url = "https://speech.googleapis.com/v1/speech:recognize?key=" + apiKey;

        String jsonPayload = "{\"config\": {\"encoding\":\"LINEAR16\",\"languageCode\":\"ja-JP\"},\"audio\": {\"content\": \"" + audioContent + "\"}}";

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return response.toString();
    }

    private static String parseTranscript(String response) {
        // 解析 JSON 响应，获取识别结果
        // 这里需要根据 Speech-to-Text API 返回的 JSON 结构进行解析，请根据实际情况进行处理
        // 下面是一个简单的示例，实际使用中需要根据返回的 JSON 结构做相应的处理
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray results = jsonObject.getJSONArray("results");
        if (results.size() > 0) {
            JSONObject result = results.getJSONObject(0);
            JSONArray alternatives = result.getJSONArray("alternatives");
            if (alternatives.size() > 0) {
                JSONObject alternative = alternatives.getJSONObject(0);
                return alternative.getString("transcript");
            }
        }
        return "";
    }
}
