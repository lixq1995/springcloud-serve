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
        System.out.println(studentList);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "成功";
    }

    /**
     * PROPAGATION_REQUIRED -- 支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择。
     * PROPAGATION_SUPPORTS -- 支持当前事务，如果当前没有事务，就以非事务方式执行。
     * PROPAGATION_MANDATORY -- 支持当前事务，如果当前没有事务，就抛出异常。
     * PROPAGATION_REQUIRES_NEW -- 新建事务，如果当前存在事务，把当前事务挂起。
     * PROPAGATION_NOT_SUPPORTED -- 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
     * PROPAGATION_NEVER -- 以非事务方式执行，如果当前存在事务，则抛出异常。
     * PROPAGATION_NESTED -- 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则进行与PROPAGATION_REQUIRED类似的操作。
     * 前六个策略类似于EJB CMT，第七个（PROPAGATION_NESTED）是Spring所提供的一个特殊变量。
     * 它要求事务管理器或者使用JDBC 3.0 Savepoint API提供嵌套事务行为（如Spring的DataSourceTransactionManager）
     * ————————————————
     *
     * https://blog.csdn.net/yanxin1213/article/details/100582643
     */
}
