package com.vegeta.global.model;

/**
 * 线程参数
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public interface PoolParameter {

    /**
     * tenantId
     */
    String getTenantId();

    /**
     * itemId
     */
    String getAppId();

    /**
     * tpId
     */
    String getThreadPoolId();

    /**
     * coreSize
     */
    Integer getCoreSize();

    /**
     * maxSize
     */
    Integer getMaxSize();

    /**
     * queueType
     */
    Integer getQueueType();

    /**
     * capacity
     */
    Integer getCapacity();

    /**
     * keepAliveTime
     */
    Integer getKeepAliveTime();

    /**
     * rejectedType
     */
    Integer getRejectedType();

    /**
     * isAlarm
     */
    Integer getIsAlarm();

    /**
     * capacityAlarm
     */
    Integer getCapacityAlarm();

    /**
     * livenessAlarm
     */
    Integer getLivenessAlarm();
}