package com.vegeta.logrecord.service.impl;

import com.vegeta.logrecord.model.Operator;
import com.vegeta.logrecord.service.OperatorGetService;

/**
 * 默认实现.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public class DefaultOperatorGetServiceImpl implements OperatorGetService {

    @Override
    public Operator getUser() {
        return new Operator("994924");
    }
}