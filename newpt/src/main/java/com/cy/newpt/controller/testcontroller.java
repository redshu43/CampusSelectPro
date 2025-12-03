package com.cy.newpt.controller;

import com.cy.newpt.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testcontroller {
    @Autowired
    private RedisUtil redisUtil;
    @GetMapping("/test-redis")
    public String testRedis() {
        redisUtil.set("hello", "world");
        String value = redisUtil.get("hello");
        return "Redis连接成功，读取到值：" + value;
    }
}
