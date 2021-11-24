package com.vegeta.global.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 实例信息
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class InstanceInfo {

    private static final String UNKNOWN = "unknown";

    // 应用名称
    private String appName = UNKNOWN;

    // hostname
    private String hostName;

    // 分组key
    private String groupKey;

    private String port;

    // 实例id
    private String instanceId;

    private String ipApplicationName;

    private String clientBasePath;

    // 客户端的对应回调地址 (应用于配置变更回调)
    private String callBackUrl;

    // 身份认证
    private String identify;

    // 会员地址信息
    private volatile String vipAddress;

    private volatile String secureVipAddress;

    // 动作类型  注册  掉线
    private volatile ActionType actionType;

    // 实例信息是否离线
    private volatile boolean isInstanceInfoDirty = false;

    // 最近更新时间
    private volatile Long lastUpdatedTimestamp;

    // 最近离线时间
    private volatile Long lastDirtyTimestamp;

    // 实例状态 默认在线
    private volatile InstanceStatus status = InstanceStatus.UP;

    // 实例状态  是否覆盖
    private volatile InstanceStatus overriddenStatus = InstanceStatus.UNKNOWN;

    public InstanceInfo() {
        this.lastUpdatedTimestamp = System.currentTimeMillis();
        this.lastDirtyTimestamp = lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp() {
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }

    public Long getLastDirtyTimestamp() {
        return lastDirtyTimestamp;
    }

    public synchronized void setOverriddenStatus(InstanceStatus status) {
        if (this.overriddenStatus != status) {
            this.overriddenStatus = status;
        }
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public synchronized void setIsDirty() {
        isInstanceInfoDirty = true;
        lastDirtyTimestamp = System.currentTimeMillis();
    }

    public synchronized long setIsDirtyWithTime() {
        setIsDirty();
        return lastDirtyTimestamp;
    }

    public synchronized void unsetIsDirty(long unsetDirtyTimestamp) {
        if (lastDirtyTimestamp <= unsetDirtyTimestamp) {
            isInstanceInfoDirty = false;
        }
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public enum InstanceStatus {
        // 在线
        UP,
        // 离线
        DOWN,
        // 注册中
        STARTING,
        //
        OUT_OF_SERVICE,

        UNKNOWN;

        public static InstanceStatus toEnum(String s) {
            if (s != null) {
                try {
                    return InstanceStatus.valueOf(s.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // ignore and fall through to unknown
                    log.debug("illegal argument supplied to InstanceStatus.valueOf: {}, defaulting to {}", s, UNKNOWN);
                }
            }
            return UNKNOWN;
        }
    }

    // 实例操作类型
    public enum ActionType {
        ADDED,

        MODIFIED,

        DELETED
    }

    // 重新注册对象信息
    @Data
    @Accessors(chain = true)
    public static class InstanceRenew {

        private String appName;

        private String instanceId;

        private String lastDirtyTimestamp;

        private String status;

    }

}

