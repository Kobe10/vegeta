package com.vegeta.client.tool.thread;

import com.vegeta.client.alarm.ThreadPoolAlarm;
import com.vegeta.client.core.DynamicThreadPoolExecutor;
import com.vegeta.global.util.Assert;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Abstract build threadPool template.
 *
 * @author chen.ma
 * @date 2021/7/5 21:45
 */
@Slf4j
public class AbstractBuildThreadPoolTemplate {

    /**
     * 线程池构建初始化参数
     * <p>
     * 此处本身是模版设计方法, 但是考虑创建简洁性, 移除 abstract
     * 异常参考 {@link AbstractQueuedSynchronizer#tryAcquire}
     *
     * @return
     */
    protected static ThreadPoolInitParam initParam() {
        throw new UnsupportedOperationException();
    }

    /**
     * 构建线程池
     *
     * @return
     */
    public static ThreadPoolExecutor buildPool() {
        ThreadPoolInitParam initParam = initParam();
        return buildPool(initParam);
    }

    /**
     * 构建线程池
     *
     * @return
     */
    public static ThreadPoolExecutor buildPool(ThreadPoolInitParam initParam) {
        Assert.notNull(initParam);
        ThreadPoolExecutor executorService =
                new ThreadPoolExecutorTemplate(initParam.getCorePoolNum(),
                        initParam.getMaxPoolNum(),
                        initParam.getKeepAliveTime(),
                        initParam.getTimeUnit(),
                        initParam.getWorkQueue(),
                        initParam.getThreadFactory(),
                        initParam.rejectedExecutionHandler);
        return executorService;
    }

    /**
     * 构建快速执行线程池
     *
     * @return
     */
    public static ThreadPoolExecutor buildFastPool() {
        ThreadPoolInitParam initParam = initParam();
        return buildFastPool(initParam);
    }

    /**
     * 构建快速执行线程池
     *
     * @return
     */
    public static ThreadPoolExecutor buildFastPool(ThreadPoolInitParam initParam) {
        TaskQueue<Runnable> taskQueue = new TaskQueue(initParam.getCapacity());
        FastThreadPoolExecutor fastThreadPoolExecutor =
                new FastThreadPoolExecutor(initParam.getCorePoolNum(),
                        initParam.getMaxPoolNum(),
                        initParam.getKeepAliveTime(),
                        initParam.getTimeUnit(),
                        taskQueue,
                        initParam.getThreadFactory(),
                        initParam.rejectedExecutionHandler);
        taskQueue.setExecutor(fastThreadPoolExecutor);
        return fastThreadPoolExecutor;
    }

    /**
     * 构建动态线程池
     *
     * @param initParam
     * @return
     */
    public static DynamicThreadPoolExecutor buildDynamicPool(ThreadPoolInitParam initParam) {
        Assert.notNull(initParam);
        DynamicThreadPoolExecutor executorService =
                new DynamicThreadPoolExecutor(initParam.getCorePoolNum(),
                        initParam.getMaxPoolNum(),
                        initParam.getKeepAliveTime(),
                        initParam.getTimeUnit(),
                        initParam.getWorkQueue(),
                        initParam.getThreadPoolId(),
                        initParam.getThreadFactory(),
                        initParam.getThreadPoolAlarm(),
                        initParam.getRejectedExecutionHandler());
        return executorService;
    }

    @Data
    @Accessors(chain = true)
    public static class ThreadPoolInitParam {

        /**
         * 核心线程数量
         */
        private Integer corePoolNum;

        /**
         * 最大线程数量
         */
        private Integer maxPoolNum;

        /**
         * 线程存活时间
         */
        private Long keepAliveTime;

        /**
         * 线程存活时间单位
         */
        private TimeUnit timeUnit;

        /**
         * 队列最大容量
         */
        private Integer capacity;

        /**
         * 阻塞队列
         */
        private BlockingQueue<Runnable> workQueue;

        /**
         * 线程池任务满时拒绝任务策略
         */
        private RejectedExecutionHandler rejectedExecutionHandler;

        /**
         * 创建线程工厂
         */
        private ThreadFactory threadFactory;

        /**
         * 线程 ID
         */
        private String threadPoolId;

        /**
         * 报警策略
         */
        private ThreadPoolAlarm threadPoolAlarm;

        public ThreadPoolInitParam(String threadNamePrefix, boolean isDaemon) {
            this.threadPoolId = threadNamePrefix;
            this.threadFactory = ThreadFactoryBuilder.builder()
                    .prefix(threadNamePrefix)
                    .daemon(isDaemon)
                    .build();
        }
    }

}
