package com.app.sns.aiproduct.service.impl;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GTTSExample {

    public static void main(String[] args) throws IOException {
        String text = "こんにちは、世界！";
        String language = "ja";
        String speed = "1";
        String outputFile = "output.mp3";


        String encodedText = URLEncoder.encode(text, "UTF-8");


        String apiUrl = "https://translate.google.com/translate_tts?ie=UTF-8&q="
                + encodedText + "&tl=" + language + "&ttsspeed=" + speed + "&client=tw-ob";


        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");


        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("语音文件已生成: " + outputFile);
    }
}
