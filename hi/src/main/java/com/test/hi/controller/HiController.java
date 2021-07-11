package com.test.hi.controller;

import com.test.common.enums.HelloEnum;
import com.test.common.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname HiController
 * @Description TODO
 * @Date 2021/3/31 22:39
 * @Created by Lixq
 */
@RestController
@RequestMapping("/hi")
@Api(tags = "hi控制层")
public class HiController {

    @GetMapping("/test")
    @ApiOperation("sayHi方法")
    public String sayHi(@RequestParam(value = "hi") String hi) {
//        throw new BusinessException(HelloEnum.EXCEPTION_ONE);
        return "成功";
    }
}
