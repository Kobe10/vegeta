package com.vegeta.client.core;

import com.vegeta.client.oapi.HttpAgent;

import java.util.Arrays;
import java.util.Collections;

/**
 * 线程池配置服务
 *
 * @Author fuzhiqiang
 * @Date 2021/11/29
 */
public class ThreadPoolConfigService implements ConfigService {

    private final ClientWorker clientWorker;

    // 初始化clientWork 初始化配置check任务
    public ThreadPoolConfigService(HttpAgent httpAgent, String identification) {
        clientWorker = new ClientWorker(httpAgent, identification);
    }

    @Override
    public void addListener(String tenantId, String appId, String tpId, Listener listener) {
        clientWorker.addTenantListeners(tenantId, appId, tpId, Collections.singletonList(listener));
    }

    @Override
    public String getServerStatus() {
        if (clientWorker.isHealthServer()) {
            return "UP";
        } else {
            return "DOWN";
        }
    }

}
