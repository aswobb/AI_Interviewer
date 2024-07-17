package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.util.RedisUtil;
import com.app.sns.aiproduct.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/refresh")
public class RedisController {
    @Autowired
    private RedisUtil redisUtil;
    @GetMapping("/userRedis/{key}")
    public JsonResult setoken(@PathVariable String key){
        redisUtil.setValueWithExpiry(key,1,31, TimeUnit.SECONDS);
        return JsonResult.ok();
    }
    @GetMapping("/redis1")
    public Object getoken(){
        Object lihao = redisUtil.getValue("lihao");
        return lihao;
    }

}
