package com.vegeta.meta.core;

import com.vegeta.global.model.InstanceInfo;

import java.util.List;

/**
 * 实例注册服务
 *
 * @Author fuzhiqiang
 * @Date 2021/12/8
 */
public interface InstanceRegistry<T> {

    /**
     * 实例列表
     *
     * @param appName 应用名称
     * @return java.util.List<com.vegeta.meta.core.Lease < T>>
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    List<Lease<T>> listInstance(String appName);

    /**
     * 注册不同类型实例  (变更实例的具体信息)
     *
     * @param info object
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    void register(T info);

    /**
     * @param instanceRenew {@link InstanceInfo.InstanceRenew}
     * @description: 租期续约
     * @author: fuzhiqiang
     * @date: 2021/12/8
     * @return: boolean
     */
    boolean renew(InstanceInfo.InstanceRenew instanceRenew);

    /**
     * Remove.
     */
    void remove(T info);
}