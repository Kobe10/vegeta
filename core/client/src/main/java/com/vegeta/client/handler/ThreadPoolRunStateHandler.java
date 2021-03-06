package com.vegeta.client.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;
import com.vegeta.client.core.DynamicThreadPoolExecutor;
import com.vegeta.client.core.ThreadPoolManager;
import com.vegeta.client.wapper.DynamicThreadPoolWrapper;
import com.vegeta.global.model.PoolRunStateInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池运行状态服务
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Slf4j
public class ThreadPoolRunStateHandler {

    private static InetAddress INET_ADDRESS;

    static {
        try {
            INET_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            log.error("Local IP acquisition failed.", ex);
        }
    }

    /**
     * 获取线程池运行状态信息
     *
     * @param tpId 线程池id
     * @return com.vegeta.global.model.PoolRunStateInfo
     * @Author fuzhiqiang
     * @Date 2021/12/3
     */
    public static PoolRunStateInfo getPoolRunState(String tpId) {
        DynamicThreadPoolWrapper executorService = ThreadPoolManager.getExecutorService(tpId);
        ThreadPoolExecutor pool = executorService.getExecutor();

        // 核心线程数
        int corePoolSize = pool.getCorePoolSize();
        // 最大线程数
        int maximumPoolSize = pool.getMaximumPoolSize();
        // 线程池当前线程数 (有锁)
        int poolSize = pool.getPoolSize();
        // 活跃线程数 (有锁)
        int activeCount = pool.getActiveCount();
        // 同时进入池中的最大线程数 (有锁)
        int largestPoolSize = pool.getLargestPoolSize();
        // 线程池中执行任务总数量 (有锁)
        long completedTaskCount = pool.getCompletedTaskCount();
        // 当前负载
        String currentLoad = divide(activeCount, maximumPoolSize) + "%";
        // 峰值负载
        String peakLoad = divide(largestPoolSize, maximumPoolSize) + "%";

        BlockingQueue<Runnable> queue = pool.getQueue();
        // 队列类型
        String queueType = queue.getClass().getSimpleName();
        // 队列元素个数
        int queueSize = queue.size();
        // 队列剩余容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列容量
        int queueCapacity = queueSize + remainingCapacity;

        // 内存占比: 使用内存 / 最大内存
        RuntimeInfo runtimeInfo = new RuntimeInfo();
        String memoryProportion = StrUtil.builder(
                "已分配: ", getPrintSize(runtimeInfo.getTotalMemory()),
                " / 最大可用: ", getPrintSize(runtimeInfo.getMaxMemory())
        ).toString();

        PoolRunStateInfo stateInfo = new PoolRunStateInfo();
        stateInfo.setCoreSize(corePoolSize);
        stateInfo.setMaximumSize(maximumPoolSize);
        stateInfo.setPoolSize(poolSize);
        stateInfo.setActiveSize(activeCount);
        stateInfo.setCurrentLoad(currentLoad);
        stateInfo.setPeakLoad(peakLoad);
        stateInfo.setQueueType(queueType);
        stateInfo.setQueueSize(queueSize);
        stateInfo.setQueueRemainingCapacity(remainingCapacity);
        stateInfo.setQueueCapacity(queueCapacity);
        stateInfo.setLargestPoolSize(largestPoolSize);
        stateInfo.setCompletedTaskCount(completedTaskCount);
        stateInfo.setHost(INET_ADDRESS.getHostAddress());
        stateInfo.setTpId(tpId);
        stateInfo.setMemoryProportion(memoryProportion);
        stateInfo.setFreeMemory(getPrintSize(runtimeInfo.getFreeMemory()));

        // 判断线程池类型是否为  动态线程池DynamicThreadPoolExecutor  动态线程池计算拒绝线程数量
        int rejectCount = pool instanceof DynamicThreadPoolExecutor
                ? ((DynamicThreadPoolExecutor) pool).getRejectCount()
                : -1;
        stateInfo.setRejectCount(rejectCount);

        return stateInfo;
    }

    public static int divide(int num1, int num2) {
        return ((int) (Double.parseDouble(num1 + "") / Double.parseDouble(num2 + "") * 100));
    }

    /**
     * 字节转换.
     *
     * @param size 字节大小
     */
    public static String getPrintSize(long size) {
        long covertNum = 1024;
        if (size < covertNum) {
            return size + "B";
        } else {
            size = size / covertNum;
        }
        if (size < covertNum) {
            return size + "KB";
        } else {
            size = size / covertNum;
        }
        if (size < covertNum) {
            size = size * 100;
            return (size / 100) + "." + (size % 100) + "MB";
        } else {
            size = size * 100 / covertNum;
            return (size / 100) + "." + (size % 100) + "GB";
        }
    }
}