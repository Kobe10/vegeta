package com.vegeta.logrecord.service;

import com.vegeta.logrecord.model.Operator;

/**
 * 获取操作人.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public interface OperatorGetService {

    /**
     * 获取操作人.
     */
    Operator getUser();
}