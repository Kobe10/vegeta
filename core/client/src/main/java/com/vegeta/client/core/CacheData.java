package com.vegeta.client.core;

import com.google.common.collect.Lists;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.util.ContentUtil;
import com.vegeta.global.util.MD5Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class CacheData {

    public volatile String md5;

    public volatile String content;

    public final String tenantId;

    public final String appId;

    public final String tpId;

    private volatile boolean isInitializing = true;

    /**
     * whether use local config.
     */
    private volatile boolean isUseLocalConfig = false;

    /**
     * last modify time.
     */
    private volatile long localConfigLastModified;


    private volatile String encryptedDataKey;

    private volatile long lastModifiedTs;

    /**
     * if is cache data md5 sync with the server.
     */
    private volatile boolean isSyncWithServer = false;

    /**
     * 1.first add listener.default is false;need to check. 2.receive config change notify,set false;need to check.
     * 3.last listener is remove,set to false;need to check
     *
     * @return the flag if sync with server
     */
    public boolean isSyncWithServer() {
        return isSyncWithServer;
    }

    public void setSyncWithServer(boolean syncWithServer) {
        isSyncWithServer = syncWithServer;
    }

    // 管理监听器包装类
    private final CopyOnWriteArrayList<ManagerListenerWrapper> listeners;

    public CacheData(String tenantId, String appId, String tpId) {
        this.tenantId = tenantId;
        this.appId = appId;
        this.tpId = tpId;
        // 实时获取线程池配置信息
        this.content = ContentUtil.getPoolContent(ThreadPoolManager.getPoolParameter(tpId));
        this.md5 = getMd5String(content);
        this.listeners = Lists.newCopyOnWriteArrayList();
    }

    public void addListener(Listener listener) {
        if (null == listener) {
            throw new IllegalArgumentException("listener is null");
        }
        ManagerListenerWrapper managerListenerWrap = new ManagerListenerWrapper(md5, listener);

        if (listeners.addIfAbsent(managerListenerWrap)) {
            log.info("[{}] [add-listener] ok, tenant={}, appId={}, cnt = {} ", tpId, tenantId, appId, listeners.size());
        }
    }

    public void checkListenerMd5() {
        for (ManagerListenerWrapper wrap : listeners) {
            if (!md5.equals(wrap.getLastCallMd5())) {
                safeNotifyListener(content, md5, wrap);
            }
        }
    }

    // 通知配置变更监听器  (线程安全)
    private void safeNotifyListener(String content, String md5, ManagerListenerWrapper wrap) {
        Listener listener = wrap.getListener();

        Runnable runnable = () -> {
            wrap.setLastCallMd5(md5);
            listener.receiveConfigInfo(content);
        };

        listener.getExecutor().execute(runnable);
    }

    public void setContent(String content) {
        this.content = content;
        this.md5 = getMd5String(this.content);
    }

    public static String getMd5String(String config) {
        return (null == config) ? Constants.NULL : MD5Utils.md5Hex(config, Constants.ENCODE);
    }

    public String getMd5() {
        return this.md5;
    }

    public boolean isInitializing() {
        return isInitializing;
    }

    public void setInitializing(boolean isInitializing) {
        this.isInitializing = isInitializing;
    }


    // 监听器包装器
    @Setter
    @Getter
    private static class ManagerListenerWrapper {

        String lastCallMd5 = CacheData.getMd5String(null);

        final Listener listener;

        public ManagerListenerWrapper(String md5, Listener listener) {
            this.lastCallMd5 = md5;
            this.listener = listener;
        }

        ManagerListenerWrapper(Listener listener) {
            this.listener = listener;
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj || obj.getClass() != getClass()) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            ManagerListenerWrapper other = (ManagerListenerWrapper) obj;
            return listener.equals(other.listener);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
