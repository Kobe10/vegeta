package com.xbk.core.global.exectors;

import lombok.Builder;

import java.util.concurrent.*;

public class TraceIdThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     */
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default rejected execution handler.
     */
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory.
     */
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     */
    @Builder
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }


    @Override
    public void execute(Runnable task) {
        super.execute(ThreadPoolTaskFactory.buildRunnableWithTraceId(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(ThreadPoolTaskFactory.buildRunnableWithTraceId(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ThreadPoolTaskFactory.buildCallableWithTraceId(task));
    }

}
