package com.app.sns.aiproduct;

import java.util.UUID;

public class UUIDTest {
    public static void main(String[] args) {
        // 生成随机UUID
        UUID uuid = UUID.randomUUID();
        System.out.println("Random UUID: " + uuid.toString().replaceAll("-",""));

    }
}
