package com.vegeta.logrecord.service;

/**
 * 函数解析.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public interface ParseFunction {

    /**
     * 是否先执行.
     */
    default boolean executeBefore() {
        return false;
    }

    /**
     * 函数名称.
     */
    String functionName();

    /**
     * 执行.
     *
     * @param value
     */
    String apply(String value);
}