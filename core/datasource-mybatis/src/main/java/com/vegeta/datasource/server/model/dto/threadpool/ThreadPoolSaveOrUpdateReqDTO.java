package com.vegeta.datasource.server.model.dto.threadpool;

import lombok.Data;

/**
 * Thread pool save or update req dto.
 *
 * @author chen.ma
 * @date 2021/6/30 21:23
 */
@Data
public class ThreadPoolSaveOrUpdateReqDTO {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * TpId
     */
    private String tpId;

    /**
     * appId
     */
    private String appId;

    /**
     * coreSize
     */
    private Integer coreSize;

    /**
     * maxSize
     */
    private Integer maxSize;

    /**
     * queueType
     */
    private Integer queueType;

    /**
     * capacity
     */
    private Integer capacity;

    /**
     * keepAliveTime
     */
    private Integer keepAliveTime;

    /**
     * isAlarm
     */
    private Integer isAlarm;

    /**
     * capacityAlarm
     */
    private Integer capacityAlarm;

    /**
     * livenessAlarm
     */
    private Integer livenessAlarm;

    /**
     * rejectedType
     */
    private Integer rejectedType;

}
