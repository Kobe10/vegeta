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
     *
     * @return
     */
    String getTenantId();

    /**
     * itemId
     *
     * @return
     */
    String getAppId();

    /**
     * tpId
     *
     * @return
     */
    String getTpId();

    /**
     * coreSize
     *
     * @return
     */
    Integer getCoreSize();

    /**
     * maxSize
     *
     * @return
     */
    Integer getMaxSize();

    /**
     * queueType
     *
     * @return
     */
    Integer getQueueType();

    /**
     * capacity
     *
     * @return
     */
    Integer getCapacity();

    /**
     * keepAliveTime
     *
     * @return
     */
    Integer getKeepAliveTime();

    /**
     * rejectedType
     *
     * @return
     */
    Integer getRejectedType();

    /**
     * isAlarm
     *
     * @return
     */
    Integer getIsAlarm();

    /**
     * capacityAlarm
     *
     * @return
     */
    Integer getCapacityAlarm();

    /**
     * livenessAlarm
     *
     * @return
     */
    Integer getLivenessAlarm();

}
