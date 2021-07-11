package com.test.hello.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
