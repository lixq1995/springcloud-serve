package com.test.common.config.threadfactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 * @author lixq
 */
public class ThreadPoolThreadFactory implements ThreadFactory {

    private AtomicInteger atomicInteger = new AtomicInteger();
    private String threadName;

    public ThreadPoolThreadFactory() {
        this(ThreadPoolThreadFactory.class.getSimpleName());
    }

    public ThreadPoolThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        int number = atomicInteger.getAndUpdate(p -> (p + 1) % Integer.MAX_VALUE);
        return new Thread(r, threadName + number);
    }
}
