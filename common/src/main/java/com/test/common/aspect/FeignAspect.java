package com.test.common.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.JsonObject;
import com.test.common.exception.BusinessException;
import com.test.common.result.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
public class FeignAspect {

    @Pointcut("execution(* com.test.*.feignapi.*Api.*(..))")
    public void pointCut() {
        // 扫描：空的方法体，如果确定不会执行的，需要跑出异常规范一下，或者这里为啥不会执行
        throw new BusinessException(ResultCode.FAILED);
    }

    @Around(value = "pointCut()") // 切哪儿
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        String name = methodSignature.getName();
        String requestJson = JSON.toJSONString(point.getArgs(), SerializerFeature.WriteMapNullValue);
        long startTime = System.currentTimeMillis();
        // 获取方法上apiOperation注释
        String methodApiValue = getMethodApiValue(method);
        // 获取类上api注释
        String classApiValue = getClassApiValue(method);
        log.info("request : {} {} {} {}",classApiValue,methodApiValue,name,
                requestJson == null ? null : requestJson.substring(1,requestJson.length() - 1));
        Object proceed = point.proceed();
        long costTime = System.currentTimeMillis() - startTime;
        String responseJson = JSON.toJSONString(proceed, SerializerFeature.WriteMapNullValue);
        int length = (proceed + "").length();
        if (length > 400000) {
            responseJson = "返回值过长，忽略打印";
        }
        if (costTime > 9000) {
            // 记录接口返回超过10秒的
            log.error("Exception:response : {} {} {} :耗时:{}ms,{}",classApiValue,methodApiValue,name,costTime,responseJson);
        } else {
            log.info("response : {} {} {}:耗时:{}ms,{}", classApiValue,methodApiValue,name,costTime,responseJson);
        }

        return proceed;
    }

    /**
     * 获取方法上ApiOperation注释,区分方法名称
     * @param method
     * @return
     */
    private String getMethodApiValue(Method method) {
        ApiOperation annotation = method.getAnnotation(ApiOperation.class);
        String methodValue = "";
        if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
            methodValue = annotation.value();
        }
        return methodValue;
    }

    /**
     * 获取方法上Api注释,区分方法名称
     * @param method
     * @return
     */
    private String getClassApiValue(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        Api annotation = declaringClass.getAnnotation(Api.class);
        String classValue = "";
        if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
            classValue = annotation.value();
        }
        return classValue;
    }

}
