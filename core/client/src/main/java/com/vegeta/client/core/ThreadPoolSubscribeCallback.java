package com.vegeta.client.core;

/**
 * 线程池的订阅服务的回调器
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public interface ThreadPoolSubscribeCallback {

    /**
     * Callback.
     *
     * @param config
     */
    void callback(String config);
}
