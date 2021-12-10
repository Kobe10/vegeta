package com.vegeta.client.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 线程池报警组件
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Data
@AllArgsConstructor
public class ThreadPoolAlarm {

    /**
     * isAlarm
     */
    private Boolean isAlarm;

    /**
     * livenessAlarm
     */
    private Integer livenessAlarm;

    /**
     * capacityAlarm
     */
    private Integer capacityAlarm;
}