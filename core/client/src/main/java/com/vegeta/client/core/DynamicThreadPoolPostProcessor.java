package com.vegeta.client.core;

import com.alibaba.fastjson.JSON;
import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.client.oapi.HttpAgent;
import com.vegeta.client.tool.ThreadPoolBuilder;
import com.vegeta.client.tool.thread.QueueTypeEnum;
import com.vegeta.client.tool.thread.RejectedTypeEnum;
import com.vegeta.client.wapper.DynamicThreadPoolWrapper;
import com.vegeta.global.config.ApplicationContextHolder;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.model.PoolParameterInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 动态线程池  后置处理器
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
@Slf4j
@AllArgsConstructor
public final class DynamicThreadPoolPostProcessor implements BeanPostProcessor {

    private final BootstrapProperties properties;

    private final HttpAgent httpAgent;

    private final ThreadPoolOperation threadPoolOperation;

    private final ExecutorService executorService = ThreadPoolBuilder.builder()
            .poolThreadSize(2, 4)
            .keepAliveTime(0L, TimeUnit.MILLISECONDS)
            .workQueue(QueueTypeEnum.ARRAY_BLOCKING_QUEUE, 1)
            .threadFactory("dynamic-threadPool-config")
            .rejected(new ThreadPoolExecutor.DiscardOldestPolicy())
            .build();

    // 重写后置处理器方法   check所有bean
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 检测 DynamicThreadPoolExecutor   find  @DynamicThreadPool 注解
        // 进行包装，订阅当前线程池的配置信息
        if (bean instanceof DynamicThreadPoolExecutor) {
            var dynamicThreadPool = ApplicationContextHolder.findAnnotationOnBean(beanName, DynamicThreadPool.class);
            if (Objects.isNull(dynamicThreadPool)) {
                return bean;
            }
            var dynamicExecutor = (DynamicThreadPoolExecutor) bean;

            var wrap = new DynamicThreadPoolWrapper(dynamicExecutor.getThreadPoolId(), dynamicExecutor);
            var remoteExecutor = assemblePoolInfoAndRegister(wrap);
            subscribeConfig(wrap);
            return remoteExecutor;
        } else if (bean instanceof DynamicThreadPoolWrapper) {
            // 如果是 DynamicThreadPoolWrapper    那么直接注册并且订阅配置信息
            var wrap = (DynamicThreadPoolWrapper) bean;
            registerAndSubscribe(wrap);
        }
        return bean;
    }

    /**
     * 1、向服务端注册信息，本地缓存对应配置信息 (方便后续check config update)
     * 2、订阅配置信息变更
     *
     * @param dynamicThreadPoolWrap 包装器
     * @Author fuzhiqiang
     * @Date 2021/12/4
     */
    private void registerAndSubscribe(DynamicThreadPoolWrapper dynamicThreadPoolWrap) {
        executorService.execute(() -> {
            assemblePoolInfoAndRegister(dynamicThreadPoolWrap);
            subscribeConfig(dynamicThreadPoolWrap);
        });
    }

    /**
     * 组装线程池参数，从服务端获取配置信息；
     * 如果存在配置信息，那么生成打包器，利用线程池管理器在本地进行线程池的注册；
     *
     * @param dynamicThreadPoolWrap 包装器
     * @return java.util.concurrent.ThreadPoolExecutor
     * @Author fuzhiqiang
     * @Date 2021/12/4
     */
    private ThreadPoolExecutor assemblePoolInfoAndRegister(DynamicThreadPoolWrapper dynamicThreadPoolWrap) {
        String tpId = dynamicThreadPoolWrap.getTpId();
        Map<String, String> queryStrMap = new HashMap<>(3);
        queryStrMap.put(Constants.TP_ID, tpId);
        queryStrMap.put(Constants.APP_ID, properties.getAppId());
        queryStrMap.put(Constants.NAMESPACE, properties.getNamespace());

        Result result;
        boolean isSubscribe = false;
        ThreadPoolExecutor poolExecutor = null;
        PoolParameterInfo ppi = new PoolParameterInfo();

        try {
            // path:  /v1/cs/configs  从服务端拉取配置，然后在本地创建对应线程池
            result = httpAgent.httpGetByConfig(Constants.CONFIG_DETAIL_PATH, null, queryStrMap, 5000L);
            if (result.isSuccess() && result.getData() != null && (ppi = JSON.toJavaObject((JSON) result.getData(), PoolParameterInfo.class)) != null) {
                // 创建阻塞队列  (通过spi实现)
                BlockingQueue workQueue = QueueTypeEnum.createBlockingQueue(ppi.getQueueType(), ppi.getCapacity());
                poolExecutor = (DynamicThreadPoolExecutor) ThreadPoolBuilder.builder()
                        .dynamicPool()
                        .workQueue(workQueue)
                        .threadFactory(tpId)
                        .poolThreadSize(ppi.getCoreSize(), ppi.getMaxSize())
                        .keepAliveTime(ppi.getKeepAliveTime(), TimeUnit.SECONDS)
                        .rejected(RejectedTypeEnum.createPolicy(ppi.getRejectedType()))
//                        .alarmConfig(ppi.getIsAlarm(), ppi.getCapacityAlarm(), ppi.getLivenessAlarm())
                        .build();

                dynamicThreadPoolWrap.setExecutor(poolExecutor);
                isSubscribe = true;
            }
        } catch (Exception ex) {
            poolExecutor = dynamicThreadPoolWrap.getExecutor() != null ? dynamicThreadPoolWrap.getExecutor() : CommonDynamicThreadPool.getInstance(tpId);
            dynamicThreadPoolWrap.setExecutor(poolExecutor);

            log.error("[Init pool] Failed to initialize thread pool configuration. error message :: {}", ex.getMessage());
        } finally {
            if (Objects.isNull(dynamicThreadPoolWrap.getExecutor())) {
                dynamicThreadPoolWrap.setExecutor(CommonDynamicThreadPool.getInstance(tpId));
            }

            // 设置是否订阅远端线程池配置
            dynamicThreadPoolWrap.setSubscribeFlag(isSubscribe);
        }
        // 线程池参数、打包器写入内存
        ThreadPoolManager.register(dynamicThreadPoolWrap.getTpId(), ppi, dynamicThreadPoolWrap);
        return poolExecutor;
    }

    /**
     * 订阅配置信息
     *
     * @param dynamicThreadPoolWrap 包装器
     * @Author fuzhiqiang
     * @Date 2021/12/2
     */
    private void subscribeConfig(DynamicThreadPoolWrapper dynamicThreadPoolWrap) {
        if (dynamicThreadPoolWrap.isSubscribeFlag()) {
            threadPoolOperation.subscribeConfig(dynamicThreadPoolWrap.getTpId(), executorService, ThreadPoolDynamicRefresh::refreshDynamicPool);
        }
    }
}
