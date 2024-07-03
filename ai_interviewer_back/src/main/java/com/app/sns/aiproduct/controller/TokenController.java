package com.app.sns.aiproduct.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {
    @RequestMapping("/fail")
    public ResponseEntity<String> tokenFail(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ユーザー認証に失敗しました");
    }
}
