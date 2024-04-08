package com.app.sns.aiproduct;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class TextToSpeechHTTPExample {
    public static void main(String[] args) throws IOException {
        String text = "こんにちは、AI面接官です。\n" +
                "あなたの面接を担当させていただきます。よろしくお願いいたします。\n" +
                "ではまずは、面接で重視したいポイントを教えていただけますか？\n" +
                " 技術スキル\n" +
                " コミュニケーション能力\n" +
                " SE経験\n" +
                " 全て\n" +
                "チェックボックスで選択したら送信ボタンを押してください。";
        String languageCode = "ja-JP";
        String outputFile = "output.wav";
        String apiKey = "11"; // Replace with your API key

        // Construct JSON payload
        String jsonPayload = "{\"audioConfig\":{\"audioEncoding\":\"LINEAR16\",\"effectsProfileId\":[\"small-bluetooth-speaker-class-device\"],\"pitch\":0,\"speakingRate\":1},\"input\":{\"text\":\"" + text + "\"},\"voice\":{\"languageCode\":\"" + languageCode + "\",\"name\":\"ja-JP-Standard-A\"}}";

        // Construct API URL
        String apiUrl = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + apiKey;

        try {
            // Send POST request to API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            System.out.println("response: " + response);
            JSONObject jsonObject = JSONObject.parseObject(response.toString());
            String audioContent = jsonObject.getString("audioContent");
            byte[] audioBytes = Base64.getDecoder().decode(audioContent);

            try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(audioBytes);
                System.out.println("音频文件已保存为：" + outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
