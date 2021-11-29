package com.vegeta.client.core;

import java.util.concurrent.Executor;

/**
 * 自定义监听器
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public interface Listener {


    /**
     * Get executor for execute this receive.
     *
     * @return Executor
     */
    Executor getExecutor();

    /**
     * Receive config info.
     *
     * @param configInfo config info
     */
    void receiveConfigInfo(final String configInfo);
}