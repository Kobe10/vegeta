package com.vegeta.datasource.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

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
    private static final long serialVersionUID = 1437054221686385158L;
    @Id
    @GeneratedValue
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 线程池id
     */
    private String threadPoolId;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 线程池介绍
     */
    private String threadPoolDesc;

    /**
     * 核心线程数
     */
    private Integer coreSize;

    /**
     * 最大线程数
     */
    private Integer maxSize;

    /**
     * 队列类型...
     */
    private Integer queueType;

    /**
     * 队列大小
     */
    private Integer capacity;

    /**
     * 拒绝策略
     */
    private Integer rejectedType;

    /**
     * 线程存活时间
     */
    private Integer keepAliveTime;

    /**
     * 线程池内容
     */
    @JSONField(serialize = false)
    private String content;

    /**
     * 线程池参数 md5加密
     */
    private String md5;

    /**
     * 负责人
     */
    private String owner;
}