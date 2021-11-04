package com.xbk.spring.web.taskexecutor;

import com.xbk.spring.web.config.taskexecutor.ThreadPoolTaskExecutorBaseConfig;
import com.xbk.spring.web.config.taskexecutor.ThreadPoolTaskExecutorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.xbk.spring.web.config.taskexecutor.ThreadPoolTaskExecutorConfig.CONFIG_NAME_COMMON;
import static com.xbk.spring.web.config.taskexecutor.ThreadPoolTaskExecutorConfig.CONFIG_NAME_JOB;
import static com.xbk.spring.web.taskexecutor.ThreadPoolTaskExecutorFactory.build;

/**
 * spring 线程池配置
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({ThreadPoolTaskExecutorConfig.class})
public class ThreadPoolTaskExecutorAutoConfig {

    @Bean(name = TaskBeanNameCommon.COMMON)
    @ConditionalOnMissingBean(name = TaskBeanNameCommon.COMMON)
    @Primary
    public ThreadPoolTaskExecutor commonThreadPoolTaskExecutor(
            @Qualifier(CONFIG_NAME_COMMON) ThreadPoolTaskExecutorBaseConfig configCommon) {
        return build(configCommon, "task-common");
    }

    /**
     * 初始化任务线程池
     */
    @Bean(name = TaskBeanNameCommon.JOB)
    @ConditionalOnMissingBean(name = TaskBeanNameCommon.JOB)
    public ThreadPoolTaskExecutor jobThreadPoolTaskExecutor(
            @Qualifier(CONFIG_NAME_JOB) ThreadPoolTaskExecutorBaseConfig configJob) {
        return build(configJob, "task-job");
    }

}