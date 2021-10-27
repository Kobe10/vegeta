package com.xbk.spring.web.taskexecutor;

import com.xbk.core.global.exectors.ThreadPoolTaskFactory;
import com.xbk.core.global.log.LogTraceIdLocal;
import com.xbk.spring.web.config.taskexecutor.ThreadPoolTaskExecutorBaseConfig;
import com.xbk.spring.web.log.LogUtil;
import lombok.experimental.UtilityClass;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@UtilityClass
public class ThreadPoolTaskExecutorFactory {

    /**
     * 包装 Task
     */
    private static Runnable buildRunnableWithLogTraceId(Runnable task) {
        return ThreadPoolTaskFactory.buildRunnableWithTraceId(() -> {
            LogUtil.initTraceId(LogTraceIdLocal.get());
            try {
                task.run();
            } finally {
                LogUtil.clean();
            }
        });
    }

    /**
     * 包装 callable
     */
    private static <T> Callable<T> buildCallableWithLogTraceId(Callable<T> callable) {
        return ThreadPoolTaskFactory.buildCallableWithTraceId(() -> {
            LogUtil.initTraceId(LogTraceIdLocal.get());
            T result;
            try {
                result = callable.call();
            } finally {
                LogUtil.clean();
            }
            return result;
        });
    }

    /**
     * 构造器
     *
     * @param config 配置信息
     * @param prefix 线程前缀
     * @return 线程池
     */
    public static ThreadPoolTaskExecutor build(ThreadPoolTaskExecutorBaseConfig config, String prefix) {
        return build(config, new ThreadPoolExecutor.AbortPolicy(), prefix);
    }

    /**
     * 构造器
     *
     * @param config                   配置信息
     * @param rejectedExecutionHandler 拒绝策略
     * @param prefix                   线程前缀
     * @return 线程池
     */
    public static ThreadPoolTaskExecutor build(ThreadPoolTaskExecutorBaseConfig config, RejectedExecutionHandler rejectedExecutionHandler, String prefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() {
            @Override
            public void execute(Runnable task) {
                super.execute(buildRunnableWithLogTraceId(task));
            }

            @Override
            public void execute(Runnable task, long startTimeout) {
                super.execute(buildRunnableWithLogTraceId(task), startTimeout);
            }

            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(buildRunnableWithLogTraceId(task));
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return super.submit(buildCallableWithLogTraceId(task));
            }

            @Override
            public ListenableFuture<?> submitListenable(Runnable task) {
                return super.submitListenable(buildRunnableWithLogTraceId(task));
            }

            @Override
            public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
                return super.submitListenable(buildCallableWithLogTraceId(task));
            }
        };
        if (Objects.isNull(config.getCorePoolSize())) {
            throw new IllegalArgumentException("core pool size can not be null");
        }
        executor.setCorePoolSize(config.getCorePoolSize());
        if (Objects.nonNull(config.getMaxPoolSize())) {
            executor.setMaxPoolSize(config.getMaxPoolSize());
        }
        executor.setKeepAliveSeconds(Objects.isNull(config.getKeepAliveSeconds()) ? -1 : config.getKeepAliveSeconds());
        if (Objects.isNull(config.getQueueCapacity())) {
            executor.setQueueCapacity(config.getQueueCapacity());
        }
        executor.setAllowCoreThreadTimeOut(config.getAllowCoreThreadTimeOut());
        executor.setThreadNamePrefix(prefix);
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        executor.setThreadFactory((runnable) -> {
            Thread thread = new Thread(runnable);
            String threadName = prefix + "-" + thread.getName();
            thread.setName(threadName);
            return thread;
        });
        executor.initialize();
        return executor;
    }
}       
