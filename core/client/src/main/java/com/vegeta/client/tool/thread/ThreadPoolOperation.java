package com.vegeta.client.tool.thread;

import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.client.core.ConfigService;
import com.vegeta.client.core.Listener;
import com.vegeta.client.core.ThreadPoolSubscribeCallback;

import java.util.concurrent.Executor;

/**
 * 线程池操作类
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public class ThreadPoolOperation {

    private final ConfigService configService;

    private final BootstrapProperties properties;

    public ThreadPoolOperation(BootstrapProperties properties, ConfigService configService) {
        this.properties = properties;
        this.configService = configService;
    }

    public Listener subscribeConfig(String tpId, Executor executor, ThreadPoolSubscribeCallback threadPoolSubscribeCallback) {
        Listener configListener = new Listener() {
            @Override
            public void receiveConfigInfo(String config) {
                threadPoolSubscribeCallback.callback(config);
            }

            @Override
            public Executor getExecutor() {
                return executor;
            }
        };

        configService.addListener(properties.getNamespace(), properties.getAppId(), tpId, configListener);
        return configListener;
    }
}