package com.vegeta.client.tool.thread;

import com.vegeta.client.spi.CustomRejectedExecutionHandler;
import com.vegeta.client.spi.VegetaServiceLoader;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

/**
 * Reject policy type Enum.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/2
 */
public enum RejectedTypeEnum {

    /**
     * 被拒绝任务的程序由主线程执行
     */
    CALLER_RUNS_POLICY(1, new ThreadPoolExecutor.CallerRunsPolicy()),

    /**
     * 被拒绝任务的处理程序, 抛出异常
     */
    ABORT_POLICY(2, new ThreadPoolExecutor.AbortPolicy()),

    /**
     * 被拒绝任务的处理程序, 默默地丢弃被拒绝的任务。
     */
    DISCARD_POLICY(3, new ThreadPoolExecutor.DiscardPolicy()),

    /**
     * 被拒绝任务的处理程序, 它丢弃最早的未处理请求, 然后重试
     */
    DISCARD_OLDEST_POLICY(4, new ThreadPoolExecutor.DiscardOldestPolicy()),

    /**
     * 发生拒绝事件时, 添加新任务并运行最早的任务
     */
    RUNS_OLDEST_TASK_POLICY(5, RejectedPolicies.runsOldestTaskPolicy()),

    /**
     * 使用阻塞方法将拒绝任务添加队列, 可保证任务不丢失
     */
    SYNC_PUT_QUEUE_POLICY(6, RejectedPolicies.syncPutQueuePolicy());

    /**
     * 类型
     */
    public Integer type;

    /**
     * 线程池拒绝策略
     */
    public RejectedExecutionHandler rejectedHandler;

    RejectedTypeEnum(Integer type, RejectedExecutionHandler rejectedHandler) {
        this.type = type;
        this.rejectedHandler = rejectedHandler;
    }

    static {
        VegetaServiceLoader.register(CustomRejectedExecutionHandler.class);
    }

    public static RejectedExecutionHandler createPolicy(int type) {
        Optional<RejectedExecutionHandler> rejectedTypeEnum = Stream.of(RejectedTypeEnum.values())
                .filter(each -> Objects.equals(type, each.type))
                .map(each -> each.rejectedHandler)
                .findFirst();

        // 使用 SPI 匹配拒绝策略
        return rejectedTypeEnum.orElseGet(() -> {
            // spi 获取CustomRejectedExecutionHandler 所有实现类
            Collection<CustomRejectedExecutionHandler> customRejectedExecutionHandlers = VegetaServiceLoader
                    .getSingletonServiceInstances(CustomRejectedExecutionHandler.class);
            Optional<RejectedExecutionHandler> customRejected = customRejectedExecutionHandlers.stream()
                    .filter(each -> Objects.equals(type, each.getType()))
                    .map(CustomRejectedExecutionHandler::generateRejected)
                    .findFirst();

            return customRejected.orElse(ABORT_POLICY.rejectedHandler);
        });
    }

    public static String getRejectedNameByType(int type) {
        Optional<RejectedTypeEnum> rejectedTypeEnum = Arrays.stream(RejectedTypeEnum.values())
                .filter(each -> each.type == type).findFirst();

        return rejectedTypeEnum.map(each -> each.rejectedHandler.getClass().getSimpleName()).orElse("");
    }

}
