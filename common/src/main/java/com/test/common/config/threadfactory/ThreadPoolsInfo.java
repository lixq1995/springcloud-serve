package com.test.common.config.threadfactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池信息模型
 * @author lixq
 */
public class ThreadPoolsInfo {

    /**
     * @author yunzhong
     *
     */
    public static class ThreadPoolInfo {
        private int corePoolSize;
        private int maximumPoolSize;
        private int activeCount;
        private int remainingCapacity;
        private int waitingSize;

        public ThreadPoolInfo(ThreadPoolExecutor threadPoolExecutor) {
            corePoolSize = threadPoolExecutor.getCorePoolSize();
            maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
            activeCount = threadPoolExecutor.getActiveCount();
            remainingCapacity = threadPoolExecutor.getQueue().remainingCapacity();
            waitingSize = threadPoolExecutor.getQueue().size();
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getActiveCount() {
            return activeCount;
        }

        public void setActiveCount(int activeCount) {
            this.activeCount = activeCount;
        }

        public int getRemainingCapacity() {
            return remainingCapacity;
        }

        public void setRemainingCapacity(int remainingCapacity) {
            this.remainingCapacity = remainingCapacity;
        }

        public int getWaitingSize() {
            return waitingSize;
        }

        public void setWaitingSize(int waitingSize) {
            this.waitingSize = waitingSize;
        }

    }

    private int totalActiveCount;
    private int totalWaitingSize;

    private Map<String, ThreadPoolInfo> infos = new HashMap<>();

    public int getTotalActiveCount() {
        return totalActiveCount;
    }

    public void setTotalActiveCount(int totalActiveCount) {
        this.totalActiveCount = totalActiveCount;
    }

    public int getTotalWaitingSize() {
        return totalWaitingSize;
    }

    public void setTotalWaitingSize(int totalWaitingSize) {
        this.totalWaitingSize = totalWaitingSize;
    }

    public Map<String, ThreadPoolInfo> getInfos() {
        return infos;
    }

    public void setInfos(Map<String, ThreadPoolInfo> infos) {
        this.infos = infos;
    }

    public void appendActiveCount(int activeCount) {
        this.totalActiveCount += activeCount;
    }

    public void appendWaitingSize(int waitingSize) {
        this.totalWaitingSize += waitingSize;
    }
}
