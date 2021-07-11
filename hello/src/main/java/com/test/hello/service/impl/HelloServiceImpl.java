package com.test.hello.service.impl;

import com.test.hello.feignapi.HiApi;
import com.test.hello.service.IHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by Lixq
 * @Classname HelloServiceImpl
 * @Description TODO
 * @Date 2021/4/1 20:59
 */
@Service
public class HelloServiceImpl implements IHelloService {

    @Autowired
    private HiApi hiApi;

    public String getHi(String hi) {
        return hiApi.sayHi(hi);
    }
}
