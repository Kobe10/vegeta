package com.vegeta.client.core;

import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.toolkit.ContentUtil;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.web.base.Result;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.vegeta.client.oapi.HttpAgent;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.util.ThreadUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static cn.hippo4j.common.constant.Constants.*;

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
        this.agent = httpAgent;
        this.identification = identification;
        this.timeout = Constants.CONFIG_LONG_POLL_TIMEOUT;
        //
        this.executor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setName("client.worker.executor");
            t.setDaemon(true);
            return t;
        });
        // 轮询任务
        this.executorService = Executors.newScheduledThreadPool(ThreadUtils.getSuitableThreadCount(1), r -> {
            Thread t = new Thread(r);
            t.setName("client.long.polling.executor");
            t.setDaemon(true);
            return t;
        });
        // 登录校验权限

        // 延迟任务  检验配置更新
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
        int longingTaskCount = (int) Math.ceil(listenerSize / perTaskConfigSize);

        if (longingTaskCount > currentLongingTaskCount) {
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
            checkStatus();

            List<CacheData> cacheDataList = new ArrayList();
            List<String> inInitializingCacheList = new ArrayList();
            cacheMap.forEach((key, val) -> cacheDataList.add(val));

            List<String> changedTpIds = checkUpdateDataIds(cacheDataList, inInitializingCacheList);
            for (String each : changedTpIds) {
                String[] keys = StrUtil.split(each, GROUP_KEY_DELIMITER);
                String tpId = keys[0];
                String itemId = keys[1];
                String namespace = keys[2];

                try {
                    String content = getServerConfig(namespace, itemId, tpId, 3000L);
                    CacheData cacheData = cacheMap.get(tpId);
                    String poolContent = ContentUtil.getPoolContent(JSON.parseObject(content, PoolParameterInfo.class));
                    cacheData.setContent(poolContent);
                } catch (Exception ex) {
                    // ignore
                }
            }

            for (CacheData cacheData : cacheDataList) {
                if (!cacheData.isInitializing() || inInitializingCacheList
                        .contains(GroupKey.getKeyTenant(cacheData.tpId, cacheData.itemId, cacheData.tenantId))) {
                    cacheData.checkListenerMd5();
                    cacheData.setInitializing(false);
                }
            }

            inInitializingCacheList.clear();
            executorService.execute(this);
        }
    }

    private List<String> checkUpdateDataIds(List<CacheData> cacheDataList, List<String> inInitializingCacheList) {
        StringBuilder sb = new StringBuilder();
        for (CacheData cacheData : cacheDataList) {
            sb.append(cacheData.tpId).append(WORD_SEPARATOR);
            sb.append(cacheData.itemId).append(WORD_SEPARATOR);
            sb.append(cacheData.tenantId).append(WORD_SEPARATOR);
            sb.append(identification).append(WORD_SEPARATOR);
            sb.append(cacheData.getMd5()).append(LINE_SEPARATOR);

            if (cacheData.isInitializing()) {
                inInitializingCacheList.add(GroupKey.getKeyTenant(cacheData.tpId, cacheData.itemId, cacheData.tenantId));
            }
        }

        boolean isInitializingCacheList = !inInitializingCacheList.isEmpty();
        return checkUpdateTpIds(sb.toString(), isInitializingCacheList);
    }

    public List<String> checkUpdateTpIds(String probeUpdateString, boolean isInitializingCacheList) {
        Map<String, String> params = new HashMap(2);
        params.put(PROBE_MODIFY_REQUEST, probeUpdateString);
        Map<String, String> headers = new HashMap(2);
        headers.put(LONG_PULLING_TIMEOUT, "" + timeout);

        // 确认客户端身份, 修改线程池配置时可单独修改
        headers.put(LONG_PULLING_CLIENT_IDENTIFICATION, identification);

        // told server do not hang me up if new initializing cacheData added in
        if (isInitializingCacheList) {
            headers.put(LONG_PULLING_TIMEOUT_NO_HANGUP, "true");
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

    public String getServerConfig(String namespace, String appId, String tpId, long readTimeout) {
        Map<String, String> params = new HashMap(3);
        params.put("namespace", namespace);
        params.put("itemId", appId);
        params.put("tpId", tpId);

        Result result = agent.httpGetByConfig(CONFIG_CONTROLLER_PATH, null, params, readTimeout);
        if (result.isSuccess()) {
            return result.getData().toString();
        }

        log.error("[Sub server] namespace :: {}, appId :: {}, tpId :: {}, result code :: {}",
                namespace, itemId, tpId, result.getCode());
        return NULL;
    }

    public List<String> parseUpdateDataIdResponse(String response) {
        if (StringUtils.isEmpty(response)) {
            return Collections.emptyList();
        }

        try {
            response = URLDecoder.decode(response, "UTF-8");
        } catch (Exception e) {
            log.error("[Polling resp] decode modifiedDataIdsString error", e);
        }


        List<String> updateList = new LinkedList();
        for (String dataIdAndGroup : response.split(LINE_SEPARATOR)) {
            if (!StringUtils.isEmpty(dataIdAndGroup)) {
                String[] keyArr = dataIdAndGroup.split(WORD_SEPARATOR);
                String dataId = keyArr[0];
                String group = keyArr[1];
                if (keyArr.length == 2) {
                    updateList.add(GroupKey.getKey(dataId, group));
                    log.info("[{}] [Polling resp] config changed. dataId={}, group={}", dataId, group);
                } else if (keyArr.length == 3) {
                    String tenant = keyArr[2];
                    updateList.add(GroupKey.getKeyTenant(dataId, group, tenant));
                    log.info("[Polling resp] config changed. dataId={}, group={}, tenant={}", dataId, group, tenant);
                } else {
                    log.error("[{}] [Polling resp] invalid dataIdAndGroup error {}", dataIdAndGroup);
                }
            }
        }
        return updateList;
    }

    public void addTenantListeners(String namespace, String itemId, String tpId, List<? extends Listener> listeners) {
        CacheData cacheData = addCacheDataIfAbsent(namespace, itemId, tpId);
        for (Listener listener : listeners) {
            cacheData.addListener(listener);
        }
    }

    public CacheData addCacheDataIfAbsent(String namespace, String itemId, String tpId) {
        CacheData cacheData = cacheMap.get(tpId);
        if (cacheData != null) {
            return cacheData;
        }

        cacheData = new CacheData(namespace, itemId, tpId);
        CacheData lastCacheData = cacheMap.putIfAbsent(tpId, cacheData);
        if (lastCacheData == null) {
            String serverConfig = null;
            try {
                serverConfig = getServerConfig(namespace, itemId, tpId, 3000L);
                PoolParameterInfo poolInfo = JSON.parseObject(serverConfig, PoolParameterInfo.class);
                cacheData.setContent(ContentUtil.getPoolContent(poolInfo));
            } catch (Exception ex) {
                log.error("[Cache Data] Error. Service Unavailable :: {}", ex.getMessage());
            }

            int taskId = cacheMap.size() / CONFIG_LONG_POLL_TIMEOUT;
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
