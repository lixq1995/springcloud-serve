package com.test.hey.controller;

import com.test.common.config.threadfactory.ThreadPoolFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author by Lixq
 * @Classname ThreadTestController
 * @Description TODO
 * @Date 2021/5/19 22:34
 */
@RestController
@RequestMapping("/thread")
@Api(tags = "多线程测试控制层")
public class ThreadTestController {

    @GetMapping("/test")
    @ApiOperation("测试多线程池管理")
    public String testThread() {
        ThreadPoolExecutor pool1 = ThreadPoolFactory.getPool("threadOrder");
        ThreadPoolExecutor pool2 = ThreadPoolFactory.getPool("threadOrder");
        System.out.println(pool1 == pool2);
        return "hello";
    }
}
