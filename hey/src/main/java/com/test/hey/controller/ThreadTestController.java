package com.test.hey.controller;

import com.test.common.config.threadfactory.ThreadPoolFactory;
import com.test.common.config.threadfactory.ThreadPoolsInfo;
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

    // todo https://juejin.cn/post/7104814252510150692  线程池参数详解

    @GetMapping("/test")
    @ApiOperation("测试多线程池管理")
    public String testThread() {
        ThreadPoolExecutor pool1 = ThreadPoolFactory.getPool("threadOrder");
        ThreadPoolExecutor pool2 = ThreadPoolFactory.getPool("threadOrder");
        ThreadPoolExecutor pool3 = ThreadPoolFactory.getPool("threadPayment");
        ThreadPoolFactory.shutdown("threadPayment");
        ThreadPoolExecutor pool4 = ThreadPoolFactory.getPool("threadPayment");
        System.out.println(pool1 == pool2);
        System.out.println(pool3 == pool4);
        System.out.println(pool3);
        System.out.println(pool4);
        ThreadPoolsInfo info = ThreadPoolFactory.getInfo();
        System.out.println("info is : " + info);
        return "hello";
    }
}
