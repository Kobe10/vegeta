package com.vegeta.datasource.server.model.dto.threadpool;

import lombok.Data;

@Data
public class ThreadPoolRespDTO {

    /**
     * id
     */
    private String id;

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String appId;

    /**
     * tpId
     */
    private String threadPoolId;

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
     * queueName
     */
    private String queueName;

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
