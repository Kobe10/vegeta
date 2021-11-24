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
     * Get executor.
     *
     * @return
     */
    Executor getExecutor();

    /**
     * Receive config info.
     *
     * @param configInfo
     */
    void receiveConfigInfo(String configInfo);
}