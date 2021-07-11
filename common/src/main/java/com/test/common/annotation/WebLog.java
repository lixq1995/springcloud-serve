package com.test.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author by Lixq
 * @Classname WebLog
 * @Description TODO https://www.runoob.com/w3cnote/java-annotation.html
 *              TODO https://www.cnblogs.com/quanxiaoha/p/10789843.html
 * @Date 2021/6/21 21:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface WebLog {

    /**
     * 日志描述信息
     *
     * @return
     */
    String description() default "";

    // todo 自定义注解总结：
    // 定义注解类WebLog，设置为以上
    // 创建对应切面类WebLogAspect
    // 设置注解为Pointcut切入点
    // 进行自定义的切面逻辑操作，after,before,around

    // todo 通过实现ConstraintValidator完成自定义校验注解

}
