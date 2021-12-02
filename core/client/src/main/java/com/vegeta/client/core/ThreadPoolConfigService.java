package com.vegeta.client.core;

import com.vegeta.client.oapi.HttpAgent;

import java.util.Arrays;

/**
 * 线程池配置服务
 *
 * @Author fuzhiqiang
 * @Date 2021/11/29
 */
public class ThreadPoolConfigService implements ConfigService {

    private final ClientWorker clientWorker;

    public ThreadPoolConfigService(HttpAgent httpAgent, String identification) {
        clientWorker = new ClientWorker(httpAgent, identification);
    }

    @Override
    public void addListener(String tenantId, String appId, String tpId, Listener listener) {
        clientWorker.addTenantListeners(tenantId, appId, tpId, Arrays.asList(listener));
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
