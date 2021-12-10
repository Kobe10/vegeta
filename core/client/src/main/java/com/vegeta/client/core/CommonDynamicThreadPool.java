package com.vegeta.client.core;

import com.vegeta.client.tool.ThreadPoolBuilder;
import com.vegeta.client.tool.thread.QueueTypeEnum;
import com.vegeta.client.tool.thread.RejectedPolicies;
import java.util.concurrent.TimeUnit;

/**
 * 通用动态线程池 单例
 *
 * @Author fuzhiqiang
 * @Date 2021/12/2
 */
public class CommonDynamicThreadPool {
    public static DynamicThreadPoolExecutor getInstance(String threadPoolId) {
        return (DynamicThreadPoolExecutor) ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(threadPoolId)
                .poolThreadSize(3, 5)
                .keepAliveTime(1000L, TimeUnit.SECONDS)
                .rejected(RejectedPolicies.runsOldestTaskPolicy())
                .alarmConfig(1, 80, 80)
                .workQueue(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, 512)
                .build();
    }
}