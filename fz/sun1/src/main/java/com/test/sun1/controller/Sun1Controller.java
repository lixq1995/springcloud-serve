package com.test.sun1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname HiController
 * @Description TODO
 * @Date 2021/3/31 22:39
 * @Created by Lixq
 */
@RestController
@RequestMapping("/sun1")
public class Sun1Controller {

    @GetMapping("/test")
    public String getAllHrs() {
        return "test";
    }
}
