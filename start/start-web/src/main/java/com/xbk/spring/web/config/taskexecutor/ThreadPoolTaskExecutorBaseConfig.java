package com.xbk.spring.web.config.taskexecutor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 线程池配置
 *
 */
@Data
@NoArgsConstructor
public class ThreadPoolTaskExecutorBaseConfig {

    /**
     * 核心线程数
     */
    private Integer corePoolSize = 10;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize = 50;

    /**
     * 线程空闲时间
     */
    private Integer keepAliveSeconds = 3 * 60;

    /**
     * 队列长度
     */
    private Integer queueCapacity = 100;

    /**
     * 核心线程数是否可以被回收
     */
    private Boolean allowCoreThreadTimeOut = true;

    @Builder
    public ThreadPoolTaskExecutorBaseConfig(Integer corePoolSize, Integer maxPoolSize, Integer keepAliveSeconds, Integer queueCapacity, Boolean allowCoreThreadTimeOut) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveSeconds = keepAliveSeconds;
        this.queueCapacity = queueCapacity;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }
}
