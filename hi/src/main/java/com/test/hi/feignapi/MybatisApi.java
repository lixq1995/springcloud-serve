package com.test.hi.feignapi;

import com.test.common.result.CommonPage;
import com.test.common.result.ResultBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Classname MybatisApi
 * @Description TODO
 * @Date 2021/4/1 20:37
 * @author by Lixq
 */
@FeignClient(value = "service-hello")
@Api(value = "hi feign 接口")
public interface MybatisApi {

    @RequestMapping(value = "/mybatis/getStudentList",method = RequestMethod.POST)
    @ApiOperation(value = "sayHi 接口")
    ResultBean<CommonPage<Object>> getStudentList(@RequestBody QueryStudent queryStudent);
}
