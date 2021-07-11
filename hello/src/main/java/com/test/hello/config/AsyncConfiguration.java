package com.test.hello.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author by Lixq
 * @Classname AsyncConfiguration
 * @Description TODO
 * @Date 2021/7/9 20:19
 */
@Slf4j
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    private int corePoolSize = 10;

    private int maxPoolSize = 200;

    private int queueCapacity = 1000;

    private int keepAliveSeconds = 30;

    private String threadNamePrefix = "stock_";

    /**
     * 线程池配置
     * @param
     * @return java.util.concurrent.Executor
     */
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        log.info("---------- 线程池开始加载 ----------");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程池大小
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        // 队列容量
        threadPoolTaskExecutor.setQueueCapacity(keepAliveSeconds);
        // 活跃时间
        threadPoolTaskExecutor.setKeepAliveSeconds(queueCapacity);
        // 线程名字前缀
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        // RejectedExecutionHandler:当pool已经达到max-size的时候，如何处理新任务
        // CallerRunsPolicy:不在新线程中执行任务，而是由调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        threadPoolTaskExecutor.initialize();
        log.info("---------- 线程池加载完成 ----------");
        return threadPoolTaskExecutor;
    }
}

