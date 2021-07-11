package com.test.hello.feignapi.feignhystrix;

import com.test.hello.feignapi.HiApi;
import org.springframework.stereotype.Component;

/**
 * @author by Lixq
 * @Classname HiApiHystrix
 * @Description TODO
 * @Date 2021/4/12 22:08
 */
@Component
public class HiApiHystrix implements HiApi{

    @Override
    public String sayHi(String hi) {
        return "sorry "+ hi;
    }

}
