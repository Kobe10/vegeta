package com.vegeta.client.core;

import cn.hutool.core.util.StrUtil;
import com.vegeta.client.oapi.HttpAgent;
import com.vegeta.client.tool.ThreadPoolBuilder;
import com.vegeta.client.tool.thread.ThreadFactoryBuilder;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import com.vegeta.global.http.result.exception.ErrorCodeEnum;
import com.vegeta.global.model.InstanceInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static com.vegeta.global.consts.Constants.THREAD_REGISTER_CLIENT_HEARTBEAT_EXECUTOR;
import static com.vegeta.global.consts.Constants.THREAD_REGISTER_CLIENT_SCHEDULER;

/**
 * 注册服务的客户端
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Slf4j
public class RegistryClient {

//    private final ThreadPoolExecutor heartbeatExecutor;

    private final ScheduledExecutorService scheduler;

    private final HttpAgent httpAgent;

    private final InstanceInfo instanceInfo;

    private volatile long lastSuccessfulHeartbeatTimestamp = -1;

    private static final String PREFIX = "RegistryClient_";

    private final String appPathIdentifier;

    public RegistryClient(HttpAgent httpAgent, InstanceInfo instanceInfo) {
        this.httpAgent = httpAgent;
        this.instanceInfo = instanceInfo;
        this.appPathIdentifier = instanceInfo.getAppName().toUpperCase() + "/" + instanceInfo.getInstanceId();
//        // 心跳 定时任务
//        this.heartbeatExecutor = ThreadPoolBuilder.builder()
//                .poolThreadSize(1, 5)
//                .keepAliveTime(0, TimeUnit.SECONDS)
//                .workQueue(new SynchronousQueue())
//                .threadFactory(THREAD_REGISTER_CLIENT_HEARTBEAT_EXECUTOR, true)
//                .build();

        // 定时任务执行器
        this.scheduler = Executors.newScheduledThreadPool(2,
                ThreadFactoryBuilder.builder()
                        .daemon(true)
                        .prefix(THREAD_REGISTER_CLIENT_SCHEDULER)
                        .build()
        );
        // 服务注册
        register();

        // init the schedule tasks     初始化之后延时30秒更新应用实例任务(定时30s一次)
        initScheduledTasks();
    }

    private void initScheduledTasks() {
        scheduler.scheduleWithFixedDelay(new HeartbeatThread(), 30, 30, TimeUnit.SECONDS);
    }

    boolean register() {
        log.info("{} {} - registering service...", PREFIX, appPathIdentifier);

        String urlPath = Constants.BASE_PATH + "/apps/register/";
        Result registerResult = null;
        try {
            registerResult = httpAgent.httpPostByDiscovery(urlPath, instanceInfo);
        } catch (Exception ex) {
            registerResult = Results.failure(ErrorCodeEnum.SERVICE_ERROR);
            log.error("{}{} - registration failed :: {}", PREFIX, appPathIdentifier, ex.getMessage(), ex);
        }

        if (log.isInfoEnabled()) {
            log.info("{}{} - registration status :: {}", PREFIX, appPathIdentifier, registerResult.isSuccess() ? "success" : "fail");
        }

        return registerResult.isSuccess();
    }

    public class HeartbeatThread implements Runnable {

        @Override
        public void run() {
            if (renew()) {
                lastSuccessfulHeartbeatTimestamp = System.currentTimeMillis();
            }
        }
    }

    /**
     * 更新实例信息
     * 更新当前实例信息  如果未找到实例信息，重新注册当前实例，并且标记当前实例信息是脏数据， 重新注册成功将数据变更为正常数据
     *
     * @return boolean
     * @Author fuzhiqiang
     * @Date 2021/11/24
     */
    boolean renew() {
        Result renewResult = null;
        try {
            InstanceInfo.InstanceRenew instanceRenew = new InstanceInfo.InstanceRenew()
                    .setAppName(instanceInfo.getAppName())
                    .setInstanceId(instanceInfo.getInstanceId())
                    .setLastDirtyTimestamp(instanceInfo.getLastDirtyTimestamp().toString())
                    .setStatus(instanceInfo.getStatus().toString());
            // 更新实例信息
            renewResult = httpAgent.httpPostByDiscovery(Constants.BASE_PATH + "/apps/renew", instanceRenew);

            if (StrUtil.equals(ErrorCodeEnum.NOT_FOUND.getCode(), renewResult.getCode())) {
                long timestamp = instanceInfo.setIsDirtyWithTime();
                boolean success = register();
                if (success) {
                    instanceInfo.unsetIsDirty(timestamp);
                }
                return success;
            }
            return renewResult.isSuccess();
        } catch (Exception ex) {
            log.error(PREFIX + "{} - was unable to send heartbeat!", appPathIdentifier, ex);
            return false;
        }
    }
}
