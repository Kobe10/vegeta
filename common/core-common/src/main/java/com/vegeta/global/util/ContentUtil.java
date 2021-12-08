package com.vegeta.global.util;

import com.alibaba.fastjson.JSON;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.model.PoolParameter;
import com.vegeta.global.model.PoolParameterInfo;

/**
 * Content util.
 *
 * @author chen.ma
 * @date 2021/6/24 16:13
 */
public class ContentUtil {

    public static String getPoolContent(PoolParameter parameter) {
        PoolParameterInfo poolInfo = new PoolParameterInfo();
        poolInfo.setTenantId(parameter.getTenantId())
                .setAppId(parameter.getAppId())
                .setThreadPoolId(parameter.getThreadPoolId())
                .setCoreSize(parameter.getCoreSize())
                .setMaxSize(parameter.getMaxSize())
                .setQueueType(parameter.getQueueType())
                .setCapacity(parameter.getCapacity())
                .setKeepAliveTime(parameter.getKeepAliveTime())
                .setIsAlarm(parameter.getIsAlarm())
                .setCapacityAlarm(parameter.getCapacityAlarm())
                .setLivenessAlarm(parameter.getLivenessAlarm())
                .setRejectedType(parameter.getRejectedType());
        return JSON.toJSONString(poolInfo);
    }

    public static String getGroupKey(PoolParameter parameter) {
        return parameter.getThreadPoolId() +
                Constants.GROUP_KEY_DELIMITER +
                parameter.getAppId() +
                Constants.GROUP_KEY_DELIMITER +
                parameter.getTenantId();
    }

    /**
     * 拼接分组key
     *
     * @param parameters 动态参数
     * @return java.lang.String
     * @Author fuzhiqiang
     * @Date 2021/11/24
     */
    public static String getGroupKey(String... parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(parameters[i]);

            if (i < parameters.length - 1) {
                stringBuilder.append(Constants.GROUP_KEY_DELIMITER);
            }
        }
        return stringBuilder.toString();
    }
}