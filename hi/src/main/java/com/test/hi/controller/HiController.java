package com.test.hi.controller;

import com.test.common.enums.HelloEnum;
import com.test.common.exception.BusinessException;
import com.test.common.result.CommonPage;
import com.test.common.result.ResultBean;
import com.test.hi.feignapi.MybatisApi;
import com.test.hi.feignapi.QueryStudent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private MybatisApi mybatisApi;

    @GetMapping("/test")
    @ApiOperation("sayHi方法")
    @Transactional(rollbackFor = Exception.class/*,propagation = Propagation.NESTED*//*,isolation = Isolation.READ_UNCOMMITTED*/)
    public String sayHi(@RequestParam(value = "hi") String hi) {
//        throw new BusinessException(HelloEnum.EXCEPTION_ONE);
        System.out.println("1");
        QueryStudent queryStudent = new QueryStudent();
        queryStudent.setKeyword(hi);
        // todo 查询方法加上事物传播级别propagation = Propagation.NESTED，即可查询到hello服务未提交的事物
        // https://segmentfault.com/a/1190000013341344
        ResultBean<CommonPage<Object>> studentList = mybatisApi.getStudentList(queryStudent);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "成功";
    }
}
