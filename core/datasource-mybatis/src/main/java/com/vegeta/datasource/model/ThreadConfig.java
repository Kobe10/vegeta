package com.vegeta.datasource.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 线程配置实体
 *
 * @author fuzhiqiang
 * @version 1.0
 * @date 2021/12/07 21:37:33
 */
@Data
public class ThreadConfig implements Serializable {
    @Id
    @GeneratedValue
    /**
     * id
     */
    @Column(name = "id")
    private Long id;

    /**
     * 线程池id
     */
    @Column(name = "thread_pool_id")
    private String threadPoolId;

    /**
     * 租户id
     */
    @Column(name = "tenant_id")
    private String tenantId;

    /**
     * 应用id
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 线程池名称
     */
    @Column(name = "thread_pool_name")
    private String threadPoolName;

    /**
     * 线程池介绍
     */
    @Column(name = "thread_pool_desc")
    private String threadPoolDesc;

    /**
     * 核心线程数
     */
    @Column(name = "core_size")
    private Integer coreSize;

    /**
     * 最大线程数
     */
    @Column(name = "max_size")
    private Integer maxSize;

    /**
     * 队列类型...
     */
    @Column(name = "queue_type")
    private Integer queueType;

    /**
     * 队列大小
     */
    @Column(name = "capacity")
    private Integer capacity;

    /**
     * 拒绝策略
     */
    @Column(name = "rejected_type")
    private Integer rejectedType;

    /**
     * 线程存活时间
     */
    @Column(name = "keep_alive_time")
    private Integer keepAliveTime;

    /**
     * 线程池内容
     */
    @Column(name = "content")
    private String content;

    /**
     * 线程池参数 md5加密
     */
    @Column(name = "md5")
    private String md5;

    /**
     * 负责人
     */
    @Column(name = "owner")
    private String owner;
}