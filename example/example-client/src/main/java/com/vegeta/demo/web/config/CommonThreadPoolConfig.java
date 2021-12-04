package com.vegeta.demo.web.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vegeta.client.core.DynamicThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置示例
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
@Slf4j
@Configuration
public class CommonThreadPoolConfig {


    // todo   现阶段代码入侵性太强   可以通过注解来绑定线程池

    /**
     * 核心线程池数
     */
    private final int CORE_POOL_SIZE = 4;

    /**
     * 允许核心线程池超时
     */
    private final boolean ALLOW_CORE_THREAD_TIME_OUT = true;

    /**
     * 队列最大存储数量
     */
    private final int QUEUE_CAPACITY = 2000;

    /**
     * 最大线程池数
     */
    private final int MAX_POOL_SIZE = 8;

    /**
     * 过期时间
     */
    private final int KEEP_ALIVE_SECONDS = 30 * 60;

    /**
     * 线程池中线程的前缀名称
     */
    private final String THREAD_NAME_PREFIX = "jobScheduler";

    @Bean
    @DynamicThreadPool
    public ThreadPoolTaskExecutor jobScheduler() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(ALLOW_CORE_THREAD_TIME_OUT);
        threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("JobScheduler-%d").build();
        threadPoolTaskExecutor.setThreadFactory(factory);
        return threadPoolTaskExecutor;
    }

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor jobScheduler1() {
        ThreadPoolExecutor threadPoolTaskExecutor = new ThreadPoolExecutor(1,1,1,null,null);
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
//        threadPoolTaskExecutor.seta(ALLOW_CORE_THREAD_TIME_OUT);
//        threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
//        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
//        threadPoolTaskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
//        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("JobScheduler-%d").build();
        threadPoolTaskExecutor.setThreadFactory(factory);
        return threadPoolTaskExecutor;
    }
}