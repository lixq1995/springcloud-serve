package com.test.hey.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname HelloController
 * @Description TODO
 * @Date 2021/4/1 20:11
 * @Created by Lixq
 */
@RestController
@RequestMapping("/hey")
@Api(tags = "hey控制层")
public class HeyController {

    @GetMapping("/test")
    @ApiOperation("获取hey数据")
    public String getAllHrs() {
        return "hey";
    }

}
