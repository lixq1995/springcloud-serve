package com.test.hello.feignapi;

import com.test.hello.feignapi.feignhystrix.HiApiHystrix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Classname HiApi
 * @Description TODO
 * @Date 2021/4/1 20:37
 * @author by Lixq
 */
@FeignClient(value = "service-hi",fallback = HiApiHystrix.class)
@Api(value = "hi feign 接口")
public interface HiApi {

    @RequestMapping(value = "/hi/test",method = RequestMethod.GET)
    @ApiOperation(value = "sayHi 接口")
    String sayHi(@RequestParam(value = "hi")String hi);
}
