package com.vegeta.client.oapi;

import com.vegeta.global.http.result.base.Result;

import java.util.Map;

/**
 * http agent 代理
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public interface HttpAgent {

    /**
     * Start.
     */
    void start();

    /**
     * Get tenant id.
     *
     * @return
     */
    String getTenantId();

    /**
     * Get encode.
     *
     * @return
     */
    String getEncode();

    /**
     * Send HTTP post request by discovery.
     *
     * @param path
     * @param body
     * @return
     */
    Result httpPostByDiscovery(String path, Object body);

    /**
     * Send HTTP get request by dynamic config.
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpGetByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                           long readTimeoutMs);

    /**
     * Send HTTP post request by dynamic config.
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpPostByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                            long readTimeoutMs);
    /**
     * Send HTTP delete request by dynamic config.
     *
     * @param path
     * @param headers
     * @param paramValues
     * @param readTimeoutMs
     * @return
     */
    Result httpDeleteByConfig(String path, Map<String, String> headers, Map<String, String> paramValues,
                              long readTimeoutMs);
}
