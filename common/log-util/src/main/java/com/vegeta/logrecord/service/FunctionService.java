package com.vegeta.logrecord.service;

/**
 * 函数服务.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public interface FunctionService {

    /**
     * 执行.
     *
     * @param functionName
     * @param value
     * @return
     */
    String apply(String functionName, String value);

    /**
     * 是否提前执行.
     *
     * @param functionName
     * @return
     */
    boolean beforeFunction(String functionName);
}