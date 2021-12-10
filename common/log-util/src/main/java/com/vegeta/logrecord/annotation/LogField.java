package com.vegeta.logrecord.annotation;

/**
 * 日志字段, 用于标记需要比较的实体属性.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public @interface LogField {

    /**
     * 字段名称
     *
     * @return
     */
    String name();

}
