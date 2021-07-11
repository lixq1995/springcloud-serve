package com.test.hello;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;


/**
 * @EnableFeignClients 开启feign功能，不加该注解提示
 * Consider defining a bean of type 'com.test.hey.feignapi.HiApi' in your configuration报错
 * @EnableDiscoveryClient 向注册中心注册
 * scanBasePackages = "com.test"  不加该行则common包中的全局异常，swagger等配置不生效
 * @EnableAsync 开启 @Async 的异步线程功能
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.test")
@EnableAsync
//@MapperScan("com.test.hello.mapper")
public class HelloApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }


//    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }
}