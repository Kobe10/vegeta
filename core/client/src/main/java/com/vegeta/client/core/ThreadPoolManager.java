package com.vegeta.client.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vegeta.client.wapper.DynamicThreadPoolWrapper;
import com.vegeta.global.model.PoolParameter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程池管理器
 *
 * @Author fuzhiqiang
 * @Date 2021/11/29
 */
public class ThreadPoolManager {

    // 线程池内存信息  key: 线程池id   value:
    private static final Map<String, PoolParameter> POOL_PARAMETER = Maps.newConcurrentMap();

    private static final Map<String, DynamicThreadPoolWrapper> EXECUTOR_MAP = Maps.newConcurrentMap();

    public static DynamicThreadPoolWrapper getExecutorService(String tpId) {
        return EXECUTOR_MAP.get(tpId);
    }

    public static PoolParameter getPoolParameter(String tpId) {
        return POOL_PARAMETER.get(tpId);
    }

    public static void register(String tpId, PoolParameter poolParameter, DynamicThreadPoolWrapper executor) {
        registerPool(tpId, executor);
        registerPoolParameter(tpId, poolParameter);
    }

    public static void registerPool(String tpId, DynamicThreadPoolWrapper executor) {
        EXECUTOR_MAP.put(tpId, executor);
    }

    public static void registerPoolParameter(String tpId, PoolParameter poolParameter) {
        POOL_PARAMETER.put(tpId, poolParameter);
    }

    public static List<String> listThreadPoolId() {
        return Lists.newArrayList(POOL_PARAMETER.keySet());
    }
}
