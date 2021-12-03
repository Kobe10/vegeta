package com.vegeta.logrecord.service.impl;

import com.google.common.collect.Maps;
import com.vegeta.logrecord.service.ParseFunction;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 函数解析工厂.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public class ParseFunctionFactory {

    private Map<String, ParseFunction> allFunctionMap;

    public ParseFunctionFactory(List<ParseFunction> parseFunctions) {
        if (CollectionUtils.isEmpty(parseFunctions)) {
            return;
        }

        allFunctionMap = Maps.newHashMap();
        for (ParseFunction parseFunction : parseFunctions) {
            if (StringUtils.isEmpty(parseFunction.functionName())) {
                continue;
            }

            allFunctionMap.put(parseFunction.functionName(), parseFunction);
        }
    }

    /**
     * 获取函数实例.
     *
     * @param functionName
     * @return
     */
    public ParseFunction getFunction(String functionName) {
        return allFunctionMap.get(functionName);
    }

    /**
     * 是否提前执行.
     *
     * @param functionName
     * @return
     */
    public boolean isBeforeFunction(String functionName) {
        return allFunctionMap.get(functionName) != null && allFunctionMap.get(functionName).executeBefore();
    }
}