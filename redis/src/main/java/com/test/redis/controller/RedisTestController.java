package com.test.redis.controller;

import com.test.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by Lixq
 * @Classname RedisTestController
 * @Description TODO
 * @Date 2021/5/17 23:08
 */
@Slf4j
@RestController
@RequestMapping("/redis")
@Api(tags = "redis服务测试接口")
public class RedisTestController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/testExpire")
    @ApiOperation("测试redis失效")
    public void testExpire() {
        redisService.set("testExpire","测试1分钟失效",1);
        System.out.println(redisService.get("testExpire"));
    }
}
