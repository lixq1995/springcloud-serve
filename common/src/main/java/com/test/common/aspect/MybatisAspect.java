package com.test.common.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.test.common.exception.BusinessException;
import com.test.common.result.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author by Lixq
 * @Classname FeignAspect
 * @Description TODO
 * @Date 2021/4/5 20:38
 */
@Slf4j
@Component
@Aspect
public class MybatisAspect {
    // todo 测试得出，不能切jar包中的类
    @Pointcut("execution(* org.apache.ibatis.executor.CachingExecutor.update*(..))")
//    @Pointcut("execution(* com.test.*.controller.MybatisController.save*(..))")
//    @Pointcut("execution(* com.test.*.service.IMybatisService.save*(..))")
//    @Pointcut("execution(* com.alibaba.fastjson.JSON.toJSONString(java.lang.Object)) && args(obj)")
    public void pointCut() {
        // 扫描：空的方法体，如果确定不会执行的，需要跑出异常规范一下，或者这里为啥不会执行
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 环绕通知
     *
     * @param pjp
     */
    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String name = pjp.getSignature().getName();
        String requestJson = JSON.toJSONString(pjp.getArgs(), SerializerFeature.WriteMapNullValue);
        System.out.println(name + "  方法开始执行  " + "入参 ：" + requestJson);
        return pjp.proceed();
    }

}
