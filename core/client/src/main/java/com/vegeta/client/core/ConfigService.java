package com.vegeta.client.core;

/**
 * 配置服务
 *
 * @Author fuzhiqiang
 * @Date 2021/11/29
 */
public interface ConfigService {

    /**
     * 注册监听器  (配置服务的监听器  用来监听线程池的配置变化)
     *
     * @param tenantId 部门id
     * @param appId    应用id
     * @param tpId     线程池id
     * @param listener 监听器
     */
    void addListener(String tenantId, String appId, String tpId, Listener listener);

    /**
     * Get server status.
     *
     * @return
     */
    String getServerStatus();
}