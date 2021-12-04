package com.vegeta.global.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 线程池运行状态信息
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
@Getter
@Setter
public class PoolRunStateInfo implements Serializable {

    private static final long serialVersionUID = 2738431455712348039L;
    /**
     * currentLoad
     */
    private String currentLoad;

    /**
     * peakLoad
     */
    private String peakLoad;

    /**
     * tpId
     */
    private String tpId;

    /**
     * coreSize
     */
    private Integer coreSize;

    /**
     * maximumSize
     */
    private Integer maximumSize;

    /**
     * poolSize
     */
    private Integer poolSize;

    /**
     * activeSize
     */
    private Integer activeSize;

    /**
     * The maximum number of threads that enter the thread pool at the same time
     */
    private Integer largestPoolSize;

    /**
     * queueType
     */
    private String queueType;

    /**
     * queueCapacity
     */
    private Integer queueCapacity;

    /**
     * queueSize
     */
    private Integer queueSize;

    /**
     * queueRemainingCapacity
     */
    private Integer queueRemainingCapacity;

    /**
     * completedTaskCount
     */
    private Long completedTaskCount;

    /**
     * rejectCount
     */
    private Integer rejectCount;

    /**
     * host
     */
    private String host;

    /**
     * memoryProportion
     */
    private String memoryProportion;

    /**
     * freeMemory
     */
    private String freeMemory;
}
