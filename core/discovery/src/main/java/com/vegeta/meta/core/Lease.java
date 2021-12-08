package com.vegeta.meta.core;

/**
 * 租约 (主要是服务于具体实例)
 *
 * @Author fuzhiqiang
 * @Date 2021/12/8
 */
public class Lease<T> {

    // 租约可执行动作
    enum Action {
        REGISTER, CANCEL, RENEW
    }

    // 租约持有者实例信息
    private T holder;

    // 续约时间
    private long evictionTimestamp;

    private long registrationTimestamp;

    // 服务起来时间
    private long serviceUpTimestamp;

    /**
     * Make it volatile so that the expiration task would see this quicker
     */
    private volatile long lastUpdateTimestamp;

    // 租约时间
    private long duration;

    public static final int DEFAULT_DURATION_IN_SECS = 90;

    public Lease(T r) {
        holder = r;
        registrationTimestamp = System.currentTimeMillis();
        lastUpdateTimestamp = registrationTimestamp;
        duration = DEFAULT_DURATION_IN_SECS * 1000;
    }

    public void renew() {
        lastUpdateTimestamp = System.currentTimeMillis() + duration;
    }

    public void cancel() {
        if (evictionTimestamp <= 0) {
            evictionTimestamp = System.currentTimeMillis();
        }
    }

    public void serviceUp() {
        if (serviceUpTimestamp == 0) {
            serviceUpTimestamp = System.currentTimeMillis();
        }
    }

    public void setServiceUpTimestamp(long serviceUpTimestamp) {
        this.serviceUpTimestamp = serviceUpTimestamp;
    }

    public boolean isExpired() {
        return isExpired(0L);
    }

    /**
     * 是否已过期
     *
     * @param additionalLeaseMs 额外补偿租约时间
     * @return boolean
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    public boolean isExpired(long additionalLeaseMs) {
        // 过期时间大于0   或    最近更新时间 + 租约时间 + 补偿租约时间 小于当前时间
        // 过期
        return (evictionTimestamp > 0 || System.currentTimeMillis() > (lastUpdateTimestamp + duration + additionalLeaseMs));
    }

    public long getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public long getLastRenewalTimestamp() {
        return lastUpdateTimestamp;
    }

    public long getEvictionTimestamp() {
        return evictionTimestamp;
    }

    public long getServiceUpTimestamp() {
        return serviceUpTimestamp;
    }

    public T getHolder() {
        return holder;
    }
}