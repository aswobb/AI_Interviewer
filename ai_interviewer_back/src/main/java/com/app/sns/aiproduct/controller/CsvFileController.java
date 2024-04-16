//package com.app.sns.aiproduct.controller;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.*;
//
//@RestController
//public class CsvFileController {
//    @PostMapping("/upload")
//    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("File is empty");
//        }
//
//        try {
//            // 读取文件数据
//            byte[] bytes = file.getBytes();
//            // 获取文件名
//            String fileName = file.getOriginalFilename();
//            // 保存文件
//            saveFile(bytes, fileName);
//            return ResponseEntity.ok().body("File uploaded successfully: " + fileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Failed to upload file");
//        }
//    }
//
//    private void saveFile(byte[] bytes, String fileName) throws IOException {
//        try (InputStream inputStream = new ByteArrayInputStream(bytes);
//             OutputStream outputStream = new FileOutputStream(fileName)) {
//            // 将字节数组写入文件
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, length);
//            }
//        }
//    }
//}
