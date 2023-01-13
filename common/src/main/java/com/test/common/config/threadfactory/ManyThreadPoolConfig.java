package com.test.common.config.threadfactory;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置
 * @author lixq
 */
@Data
@Configuration
public class ManyThreadPoolConfig {
    private static final Logger log = LoggerFactory.getLogger(ManyThreadPoolConfig.class);

    /**
     * 正则表达式校验文件名合法，不包含字符 \ / : * ? " < > |
     */

    private static volatile ManyThreadPoolConfig poolManager;

    private int corePoolSize = 10;

    private int maximumPoolSize = 200;

    private int queueSize = 1000;

    private int keepAliveTime = 30;

    private final BlockingQueue<Runnable> taskPollingQueue = new LinkedBlockingQueue(queueSize);

    public ThreadPoolExecutor threadPool = null;

}