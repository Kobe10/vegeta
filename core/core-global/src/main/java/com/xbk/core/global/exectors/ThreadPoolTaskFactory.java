package com.xbk.core.global.exectors;

import com.xbk.core.global.log.LogTraceIdLocal;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.concurrent.*;

@UtilityClass
public class ThreadPoolTaskFactory {

    /**
     * 包装 Task
     */
    public static Runnable buildRunnableWithTraceId(Runnable task) {
        String traceId = LogTraceIdLocal.get();
        return () -> {
            if (Objects.isNull(traceId) || "".equals(traceId)) {
                LogTraceIdLocal.init();
            } else {
                LogTraceIdLocal.set(traceId);
            }
            try {
                task.run();
            } finally {
                LogTraceIdLocal.clean();
            }
        };
    }

    /**
     * 包装 callable
     */
    public static <T> Callable<T> buildCallableWithTraceId(Callable<T> callable) {
        final String traceId = LogTraceIdLocal.get();
        return () -> {
            if (Objects.isNull(traceId) || "".equals(traceId)) {
                LogTraceIdLocal.init();
            } else {
                LogTraceIdLocal.set(traceId);
            }
            T result;
            try {
                result = callable.call();
            } finally {
                LogTraceIdLocal.clean();
            }
            return result;
        };
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     */
    public static TraceIdThreadPoolExecutor build(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default rejected execution handler.
     */
    public static TraceIdThreadPoolExecutor build(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory.
     */
    public static TraceIdThreadPoolExecutor build(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     */
    public static TraceIdThreadPoolExecutor build(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
}       
