package com.vegeta.client.core;

import com.alibaba.fastjson.JSON;
import com.vegeta.client.tool.thread.QueueTypeEnum;
import com.vegeta.client.tool.thread.RejectedTypeEnum;
import com.vegeta.client.tool.thread.ResizableCapacityLinkedBlockIngQueue;
import com.vegeta.global.model.PoolParameterInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 刷新线程池的参数
 *
 * @Author fuzhiqiang
 * @Date 2021/12/2
 */
@Slf4j
public class ThreadPoolDynamicRefresh {

    public static void refreshDynamicPool(String content) {
        PoolParameterInfo parameter = JSON.parseObject(content, PoolParameterInfo.class);
//        ThreadPoolAlarmManage.sendPoolConfigChange(parameter);
        ThreadPoolDynamicRefresh.refreshDynamicPool(parameter);
    }

    /**
     * 刷新线程池参数
     *
     * @param parameter
     * @Author fuzhiqiang
     * @Date 2021/12/2
     */
    public static void refreshDynamicPool(PoolParameterInfo parameter) {
        String threadPoolId = parameter.getThreadPoolId();
        ThreadPoolExecutor executor = ThreadPoolManager.getExecutorService(threadPoolId).getExecutor();
        // 核心线程数
        int originalCoreSize = executor.getCorePoolSize();
        // 最大线程数
        int originalMaximumPoolSize = executor.getMaximumPoolSize();

        String originalQuery = executor.getQueue().getClass().getSimpleName();
        int originalCapacity = executor.getQueue().remainingCapacity() + executor.getQueue().size();
        long originalKeepAliveTime = executor.getKeepAliveTime(TimeUnit.SECONDS);
        String originalRejected = executor.getRejectedExecutionHandler().getClass().getSimpleName();

        // change
        changePoolInfo(executor, parameter);
        ThreadPoolExecutor afterExecutor = ThreadPoolManager.getExecutorService(threadPoolId).getExecutor();

        log.info("[🔥 {}] Changed thread pool. \ncoreSize :: [{}], maxSize :: [{}], queueType :: [{}], capacity :: [{}], keepAliveTime :: [{}], rejectedType :: [{}]",
                threadPoolId.toUpperCase(),
                String.format("%s => %s", originalCoreSize, afterExecutor.getCorePoolSize()),
                String.format("%s => %s", originalMaximumPoolSize, afterExecutor.getMaximumPoolSize()),
                String.format("%s => %s", originalQuery, QueueTypeEnum.getBlockingQueueNameByType(parameter.getQueueType())),
                String.format("%s => %s", originalCapacity,
                        (afterExecutor.getQueue().remainingCapacity() + afterExecutor.getQueue().size())),
                String.format("%s => %s", originalKeepAliveTime, afterExecutor.getKeepAliveTime(TimeUnit.SECONDS)),
                String.format("%s => %s", originalRejected, RejectedTypeEnum.getRejectedNameByType(parameter.getRejectedType())));
    }

    /**
     * 变更线程池参数
     *
     * @param executor  线程池执行器
     * @param parameter 参数
     * @Author fuzhiqiang
     * @Date 2021/12/2
     */
    public static void changePoolInfo(ThreadPoolExecutor executor, PoolParameterInfo parameter) {
        if (parameter.getCoreSize() != null) {
            executor.setCorePoolSize(parameter.getCoreSize());
        }

        if (parameter.getMaxSize() != null) {
            executor.setMaximumPoolSize(parameter.getMaxSize());
        }

        if (parameter.getCapacity() != null
                && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.type, parameter.getQueueType())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockIngQueue) {
                ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) executor.getQueue();
                queue.setCapacity(parameter.getCapacity());
            } else {
                log.warn("[Pool change] The queue length cannot be modified. Queue type mismatch. Current queue type :: {}",
                        executor.getQueue().getClass().getSimpleName());
            }
        }

        if (parameter.getKeepAliveTime() != null) {
            executor.setKeepAliveTime(parameter.getKeepAliveTime(), TimeUnit.SECONDS);
        }

        if (parameter.getRejectedType() != null) {
            executor.setRejectedExecutionHandler(RejectedTypeEnum.createPolicy(parameter.getRejectedType()));
        }
    }
}