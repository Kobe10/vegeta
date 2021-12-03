package com.vegeta.logrecord.service.impl;

import com.vegeta.logrecord.service.ParseFunction;

/**
 * 默认实现.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public class DefaultParseFunction implements ParseFunction {

    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        return null;
    }

    @Override
    public String apply(String value) {
        return null;
    }
}