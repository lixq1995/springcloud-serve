package com.test.hello.controller;

import com.test.hello.config.TestMap;
import com.test.hello.service.IHelloService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @Classname HelloController
 * @Description TODO
 * @Date 2021/4/1 20:11
 * @Created by Lixq
 */
@RestController
@RequestMapping("/hello")
@Api(tags = "hello控制层")
public class HelloController {

    @Autowired
    private IHelloService iHelloService;

    @GetMapping("/test")
    @ApiOperation("获取hello数据")
    public String getAllHrs() {
        // 获取HttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");
        System.out.println("token :" + token);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("1","1");
        hashMap.putAll(TestMap.getMap());
        System.out.println(hashMap);
//        int a = 3/0;
        return "hello";
    }

    @GetMapping("/getHi")
    @ApiOperation("获取hi数据")
    public String getHi(String hi) {
        String response = iHelloService.getHi(hi);
        return response;
    }
}
