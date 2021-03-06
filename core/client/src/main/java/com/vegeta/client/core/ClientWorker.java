package com.vegeta.client.core;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vegeta.client.oapi.HttpAgent;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.model.PoolParameterInfo;
import com.vegeta.global.util.ContentUtil;
import com.vegeta.global.util.GroupKeyUtil;
import com.vegeta.global.util.ThreadUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.vegeta.global.consts.Constants.*;

/**
 * 客户端  Long polling.
 *
 * @Author fuzhiqiang
 * @Date 2021/11/29
 */
@Slf4j
public class ClientWorker implements Closeable {

    private double currentLongingTaskCount = 0;

    private long timeout;

    private final HttpAgent agent;

    private final String identification;

    private String uuid = UUID.randomUUID().toString();

    private final ScheduledExecutorService executor;

    private final ScheduledExecutorService executorService;

    private AtomicBoolean isHealthServer = new AtomicBoolean(true);

    private AtomicBoolean isHealthServerTemp = new AtomicBoolean(true);

    /**
     * key: groupKey -> value: cacheData.
     */
    private final AtomicReference<Map<String, CacheData>> cacheMap = new AtomicReference<>(
            Maps.newConcurrentMap());

    @SuppressWarnings("all")
    public ClientWorker(HttpAgent httpAgent, String identification) {
        // 1、初始化agent-- ServerHttpAgent (初始化 线程池基本信息 监听器基本信息 http调用基本信息)
        this.agent = httpAgent;
        this.identification = identification;
        this.timeout = Constants.CONFIG_LONG_POLL_TIMEOUT;
        this.executor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setName("client.worker.executor");
            t.setDaemon(true);
            return t;
        });
        // 2、轮询任务
        this.executorService = Executors.newScheduledThreadPool(ThreadUtils.getSuitableThreadCount(1), r -> {
            Thread t = new Thread(r);
            t.setName("client.long.polling.executor");
            t.setDaemon(true);
            return t;
        });
        // todo 登录校验权限 (服务端登录校验)
        // login

        // 延迟任务  校验配置更新  定时10s一次
        this.executor.scheduleWithFixedDelay(() -> {
            try {
                checkConfigInfo();
            } catch (Throwable e) {
                log.error("[Sub check] rotate check error", e);
            }
        }, 1L, 10L, TimeUnit.MILLISECONDS);
    }

    public void checkConfigInfo() {
        int listenerSize = cacheMap.get().size();
        double perTaskConfigSize = 3000D;
        // 监听组的任务数量
        int longingTaskCount = (int) Math.ceil(listenerSize / perTaskConfigSize);
        // 当组的数量大于当前内存的任务数量  轮询补全长轮询任务数量
        if (longingTaskCount > currentLongingTaskCount) {
            log.info("【注册 listener 任务触发，当前轮询任务数量: {}, 实时任务数量: {}】", currentLongingTaskCount, longingTaskCount);
            for (int i = (int) currentLongingTaskCount; i < longingTaskCount; i++) {
                executorService.execute(new LongPollingRunnable());
            }
            currentLongingTaskCount = longingTaskCount;
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {

    }

    class LongPollingRunnable implements Runnable {

        @SneakyThrows
        private void checkStatus() {
            if (Objects.equals(isHealthServerTemp.get(), Boolean.FALSE)
                    && Objects.equals(isHealthServer.get(), Boolean.TRUE)) {
                isHealthServerTemp.set(Boolean.TRUE);
                log.info("🚀 The client reconnects to the server successfully.");
            }
            // 服务端状态不正常睡眠 30s
            if (!isHealthServer.get()) {
                isHealthServerTemp.set(Boolean.FALSE);
                log.error("[Check config] Error. exception message, Thread sleep 30 s.");
                Thread.sleep(30000);
            }
        }

        @Override
        @SneakyThrows
        public void run() {
            // 校验服务端状态
            checkStatus();
            // 获取客户端所有分组 线程池缓存
            List<CacheData> cacheDataList = Lists.newArrayList();
            List<String> inInitializingCacheList = Lists.newArrayList();
            cacheMap.get().forEach((key, val) -> cacheDataList.add(val));
            // 获取配置信息变更的线程池的id
            List<String> changedTpIds = checkUpdateDataIds(cacheDataList, inInitializingCacheList);
            for (String each : changedTpIds) {
                // 分解 groupKey
                List<String> keys = StrUtil.split(each, Constants.GROUP_KEY_DELIMITER);
                String tpId = keys.get(0);
                String appId = keys.get(1);
                String namespace = keys.get(2);

                try {
                    // 获取线程池配置信息，存入缓存的上下文中
                    String content = getServerConfig(namespace, appId, tpId, 3000L);
                    CacheData cacheData = cacheMap.get().get(tpId);
                    String poolContent = ContentUtil.getPoolContent(JSON.parseObject(content, PoolParameterInfo.class));
                    cacheData.setContent(poolContent);
                } catch (Exception ignored) {
                }
            }

            // 遍历当前所有的线程池的缓存信息  (初始化过的和正在初始化的)
            for (CacheData cacheData : cacheDataList) {
                if (!cacheData.isInitializing() || inInitializingCacheList
                        .contains(GroupKeyUtil.getKeyTenant(cacheData.tpId, cacheData.appId, cacheData.tenantId))) {
                    // 校验缓存中的配置信息的md5和上一次请求是否一致，  如果不一致   那么通知监听器，线程池配置发生变更
                    cacheData.checkListenerMd5();
                    cacheData.setInitializing(false);
                }
            }

            inInitializingCacheList.clear();
            executorService.execute(this);
        }
    }

    /**
     * 校验线程池配置信息
     *
     * @param cacheDataList           配置缓存
     * @param inInitializingCacheList 初始化list
     * @return java.util.List<java.lang.String>
     * @Author fuzhiqiang
     * @Date 2021/11/30
     */
    private List<String> checkUpdateDataIds(List<CacheData> cacheDataList, List<String> inInitializingCacheList) {
        StringBuilder sb = new StringBuilder();
        for (CacheData cacheData : cacheDataList) {
            sb.append(cacheData.tpId).append(Constants.WORD_SEPARATOR);
            sb.append(cacheData.appId).append(Constants.WORD_SEPARATOR);
            sb.append(cacheData.tenantId).append(Constants.WORD_SEPARATOR);
            sb.append(identification).append(Constants.WORD_SEPARATOR);
            sb.append(cacheData.getMd5()).append(Constants.LINE_SEPARATOR);

            if (cacheData.isInitializing()) {
                inInitializingCacheList.add(GroupKeyUtil.getKeyTenant(cacheData.tpId, cacheData.appId, cacheData.tenantId));
            }
        }
        boolean isInitializingCacheList = !inInitializingCacheList.isEmpty();
        return checkUpdateTpIds(sb.toString(), isInitializingCacheList);
    }

    public List<String> checkUpdateTpIds(String probeUpdateString, boolean isInitializingCacheList) {
        Map<String, String> params = new HashMap<>(2);
        params.put(Constants.PROBE_MODIFY_REQUEST, probeUpdateString);
        Map<String, String> headers = new HashMap<>(2);
        headers.put(Constants.LONG_PULLING_TIMEOUT, "" + timeout);

        // 确认客户端身份, 修改线程池配置时可单独修改
        headers.put(Constants.LONG_PULLING_CLIENT_IDENTIFICATION, identification);

        // told server do not hang me up if new initializing cacheData added in
        // 数据初始化, 增加参数 (让服务端保持连接)
        if (isInitializingCacheList) {
            headers.put(Constants.LONG_PULLING_TIMEOUT_NO_HANGUP, "true");
        }

        if (StringUtils.isEmpty(probeUpdateString)) {
            return Collections.emptyList();
        }

        try {
            long readTimeoutMs = timeout + (long) Math.round(timeout >> 1);
            Result result = agent.httpPostByConfig(LISTENER_PATH, headers, params, readTimeoutMs);

            // Server 端重启后会进入非健康状态, 不进入 catch 则为健康调用
            isHealthServer.set(true);
            if (result != null && result.isSuccess()) {
                setHealthServer(true);
                return parseUpdateDataIdResponse(result.getData().toString());
            }
        } catch (Exception ex) {
            setHealthServer(false);
            log.error("[Check update] get changed dataId exception. error message :: {}", ex.getMessage());
        }

        return Collections.emptyList();
    }

    /**
     * 获取服务端配置信息  path: /v1/cs/configs
     *
     * @param namespace   空间
     * @param appId       应用id
     * @param tpId        部门id
     * @param readTimeout 超时时间
     * @return java.lang.String
     * @Author fuzhiqiang
     * @Date 2021/11/30
     */
    public String getServerConfig(String namespace, String appId, String tpId, long readTimeout) {
        Map<String, String> params = Maps.newConcurrentMap();
        params.put("namespace", namespace);
        params.put("appId", appId);
        params.put("tpId", tpId);

        Result result = agent.httpGetByConfig(CONFIG_DETAIL_PATH, null, params, readTimeout);
        if (result.isSuccess()) {
            return result.getData().toString();
        }

        log.error("[Sub server] namespace :: {}, appId :: {}, tpId :: {}, result code :: {}",
                namespace, appId, tpId, result.getCode());
        return Constants.NULL;
    }

    /**
     * 解析服务端返回数据
     *
     * @param response 返回结果
     * @return java.util.List<java.lang.String>
     * @Author fuzhiqiang
     * @Date 2021/12/1
     */
    public List<String> parseUpdateDataIdResponse(String response) {
        if (StringUtils.isNoneBlank(response)) {
            return Collections.emptyList();
        }

        try {
            response = URLDecoder.decode(response, "UTF-8");
        } catch (Exception e) {
            log.error("[Polling resp] decode modifiedDataIdsString error", e);
        }

        List<String> updateList = Lists.newLinkedList();
        // 返回结果用字符 '1' 隔开  (包含所有配置信息)
        for (String dataIdAndGroup : response.split(Constants.LINE_SEPARATOR)) {
            if (!StringUtils.isEmpty(dataIdAndGroup)) {
                // 每个线程池的详细配置用  字符  '2' 隔开
                String[] keyArr = dataIdAndGroup.split(Constants.WORD_SEPARATOR);
                String dataId = keyArr[0];
                String group = keyArr[1];
                if (keyArr.length == 2) {
                    // 无部门的
                    updateList.add(GroupKeyUtil.getKey(dataId, group));
                    log.info(" [Polling resp] config changed. dataId={}, group={}", dataId, group);
                } else if (keyArr.length == 3) {
                    // 有部门的
                    String tenant = keyArr[2];
                    updateList.add(GroupKeyUtil.getKeyTenant(dataId, group, tenant));
                    log.info("[Polling resp] config changed. dataId={}, group={}, tenant={}", dataId, group, tenant);
                } else {
                    log.error(" [Polling resp] invalid dataIdAndGroup error {}", dataIdAndGroup);
                }
            }
        }
        return updateList;
    }


    public void addTenantListeners(String namespace, String appId, String tpId, List<? extends Listener> listeners) {
        CacheData cacheData = addCacheDataIfAbsent(namespace, appId, tpId);
        for (Listener listener : listeners) {
            cacheData.addListener(listener);
        }
    }

    public CacheData addCacheDataIfAbsent(String namespace, String itemId, String tpId) {
        CacheData cacheData = cacheMap.get().get(tpId);
        if (cacheData != null) {
            return cacheData;
        }

        cacheData = new CacheData(namespace, itemId, tpId);
        CacheData lastCacheData = cacheMap.get().putIfAbsent(tpId, cacheData);
        if (lastCacheData == null) {
            String serverConfig = null;
            try {
                serverConfig = getServerConfig(namespace, itemId, tpId, 3000L);
                PoolParameterInfo poolInfo = JSON.parseObject(serverConfig, PoolParameterInfo.class);
                cacheData.setContent(ContentUtil.getPoolContent(poolInfo));
            } catch (Exception ex) {
                log.error("[Cache Data] Error. Service Unavailable :: {}", ex.getMessage());
            }

            int taskId = cacheMap.get().size() / Constants.CONFIG_LONG_POLL_TIMEOUT;
            cacheData.setTaskId(taskId);

            lastCacheData = cacheData;
        }

        return lastCacheData;
    }

    public boolean isHealthServer() {
        return this.isHealthServer.get();
    }

    private void setHealthServer(boolean isHealthServer) {
        this.isHealthServer.set(isHealthServer);
    }

}
