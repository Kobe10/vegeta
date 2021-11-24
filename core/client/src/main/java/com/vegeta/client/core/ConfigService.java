package com.vegeta.client.core;

/**
 * 线程池配置服务
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public interface ConfigService {

    /**
     * Add listener.
     *
     * @param tenantId
     * @param itemId
     * @param tpId
     * @param listener
     */
    void addListener(String tenantId, String itemId, String tpId, Listener listener);

    /**
     * Get server status.
     *
     * @return
     */
    String getServerStatus();

}
