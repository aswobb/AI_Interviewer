package com.app.sns.aiproduct.util;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String CHARSET = "UTF-8";

    public static String encrypt(String key, String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARSET), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(CHARSET));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String key, String encryptedData) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARSET), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String key = "0123456789abcdef"; // 16 bytes key for AES-128
        String originalData = "Hello, AES!";

        // 加密
        String encryptedData = AESUtil.encrypt(key, originalData);
        System.out.println("Encrypted data: " + encryptedData);

        // 解密
        String decryptedData = AESUtil.decrypt(key, encryptedData);
        System.out.println("Decrypted data: " + decryptedData);
    }
}
