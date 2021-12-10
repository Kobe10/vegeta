package com.vegeta.demo.web.config;

import com.vegeta.client.core.DynamicThreadPool;
import com.vegeta.client.core.DynamicThreadPoolExecutor;
import com.vegeta.client.tool.ThreadPoolBuilder;
import com.vegeta.client.wapper.DynamicThreadPoolWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置示例
 * todo   现阶段代码入侵性太强   可以通过注解来绑定线程池
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
@Slf4j
@Configuration
public class SDKThreadPoolConfig {

    public static final String TASK_CONSUME = "task-consume";

    public static final String TASK_PRODUCE = "task-produce";

    /**
     * {@link DynamicThreadPoolWrapper} 完成 Server 端订阅配置功能.
     */
    @Bean
    public DynamicThreadPoolWrapper messageCenterDynamicThreadPool() {
        ThreadPoolExecutor customExecutor = ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(TASK_CONSUME)
                .build();
        return new DynamicThreadPoolWrapper(TASK_CONSUME, customExecutor);
    }

    /**
     * 通过 {@link DynamicThreadPool} 修饰 {@link DynamicThreadPoolExecutor} 完成 Server 端订阅配置功能.
     * <p>
     * 由动态线程池注解修饰后, IOC 容器中保存的是 {@link DynamicThreadPoolExecutor}
     */
    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor dynamicThreadPoolExecutor() {
        return ThreadPoolBuilder.builder()
                .threadFactory(TASK_PRODUCE)
                .dynamicPool()
                .build();
    }

}