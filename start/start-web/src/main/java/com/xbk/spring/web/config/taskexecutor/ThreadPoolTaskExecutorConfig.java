package com.xbk.spring.web.config.taskexecutor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Data
@ConfigurationProperties(prefix = ThreadPoolTaskExecutorConfig.PREFIX)
public class ThreadPoolTaskExecutorConfig {

    public static final String PREFIX = "spring.pool.task";

    /**
     * common 线程池配置前缀
     */
    public static final String PREFIX_COMMON = PREFIX + ".common";

    /**
     * common 线程池配置 bean
     */
    public static final String CONFIG_NAME_COMMON = "ThreadPoolTaskExecutorBaseConfigCommon";

    /**
     * job 线程池配置前缀
     */
    public static final String PREFIX_JOB = PREFIX + ".job";

    /**
     * job 线程池配置 bean
     */
    public static final String CONFIG_NAME_JOB = "ThreadPoolTaskExecutorBaseConfigJob";

    /**
     * 基础线程池
     */
    @Bean(CONFIG_NAME_COMMON)
    @ConfigurationProperties(prefix = ThreadPoolTaskExecutorConfig.PREFIX_COMMON)
    public ThreadPoolTaskExecutorBaseConfig common() {
        return ThreadPoolTaskExecutorBaseConfig.builder()
                .corePoolSize(10)
                .maxPoolSize(100)
                .keepAliveSeconds(5 * 60)
                .queueCapacity(100)
                .allowCoreThreadTimeOut(false)
                .build();
    }


    /**
     * 任务线程池
     */
    @Bean(CONFIG_NAME_JOB)
    @ConfigurationProperties(prefix = ThreadPoolTaskExecutorConfig.PREFIX_JOB)
    public ThreadPoolTaskExecutorBaseConfig job() {
        return ThreadPoolTaskExecutorBaseConfig.builder()
                .corePoolSize(10)
                .maxPoolSize(10)
                .keepAliveSeconds(3 * 60)
                .queueCapacity(1000000)
                .allowCoreThreadTimeOut(true)
                .build();
    }

}
