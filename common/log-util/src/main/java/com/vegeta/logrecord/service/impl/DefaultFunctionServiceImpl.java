package com.vegeta.logrecord.service.impl;

import com.vegeta.logrecord.service.FunctionService;
import com.vegeta.logrecord.service.ParseFunction;
import lombok.AllArgsConstructor;

/**
 * 默认实现函数接口.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
@AllArgsConstructor
public class DefaultFunctionServiceImpl implements FunctionService {

    private final ParseFunctionFactory parseFunctionFactory;

    @Override
    public String apply(String functionName, String value) {
        ParseFunction function = parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            return value;
        }
        return function.apply(value);
    }

    @Override
    public boolean beforeFunction(String functionName) {
        return parseFunctionFactory.isBeforeFunction(functionName);
    }

}
